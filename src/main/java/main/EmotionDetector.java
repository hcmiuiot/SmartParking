package main;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EmotionDetector {
    private static volatile EmotionDetector instance = null;
    private static ImageAnnotatorClient vision;
    private static Feature feature;
    private static List<AnnotateImageRequest> requestList = new ArrayList<>();

    private Double getArea(List<Vertex> vertices) {
        double sum = 0;
        for (int i = 0; i < vertices.size(); i++) {
            if (i == 0) {
                sum += vertices.get(i).getX() * (vertices.get(i + 1).getY() - vertices.get(vertices.size() - 1).getY());
            } else if (i == vertices.size() - 1) {
                sum += vertices.get(i).getX() * (vertices.get(0).getY() - vertices.get(i - 1).getY());
            } else {
                sum += vertices.get(i).getX() * (vertices.get(i + 1).getY() - vertices.get(i - 1).getY());
            }
        }
        double area = 0.5 * Math.abs(sum);
        return area;
    }

    private EnumEmotion getMainEmotionFromFaceAnnotation(FaceAnnotation face) {
        int joyNumber = face.getJoyLikelihood().getNumber();
        int sorrowNumber = face.getSorrowLikelihood().getNumber();
        int angerNumber = face.getAngerLikelihood().getNumber();

        System.out.println("JOY: " + joyNumber);
        System.out.println("SORROW: " + sorrowNumber);
        System.out.println("ANGER: " + angerNumber);


        if (joyNumber == sorrowNumber && sorrowNumber == angerNumber)
            return EnumEmotion.NEURAL;

        if (joyNumber == Math.max(Math.max(joyNumber, sorrowNumber), angerNumber)){
            return EnumEmotion.JOY;
        } else if (sorrowNumber == Math.max(Math.max(joyNumber, sorrowNumber), angerNumber)){
            return EnumEmotion.SORROW;
        } else if (angerNumber == Math.max(Math.max(joyNumber, sorrowNumber), angerNumber)){
            return EnumEmotion.ANGER;
        }

        return EnumEmotion.ERROR;
    }

    private EmotionDetector() {
        try {
            vision = ImageAnnotatorClient.create();
            feature = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static EmotionDetector getInstance() {
        if (instance == null) {
            synchronized (EmotionDetector.class) {
                if (null == instance) {
                    instance = new EmotionDetector();
                }
            }
        }
        return instance;
    }

    /**
     * Make request to GG Cloud Vision -> response -> take emotion of the biggest face in image
     *
     * @param imgData
     * @return EnumEmotion
     */
    public EnumEmotion getEmotion(byte[] imgData) {
        new EmotionDetector();

        ByteString imgBytes = ByteString.copyFrom(imgData);
        Image img = Image.newBuilder().setContent(imgBytes).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feature)
                .setImage(img)
                .build();
        requestList.add(request);

        BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requestList);
        List<AnnotateImageResponse> responses = response.getResponsesList();

        //Only support first responses
        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                System.out.printf("Error: %s\n", res.getError().getMessage());
                return EnumEmotion.ERROR;
            }
            // If has face in response
            if (res.getFaceAnnotationsList().size() > 0) {
                if (res.getFaceAnnotationsList().size() == 1) {
                    return getMainEmotionFromFaceAnnotation(res.getFaceAnnotations(0));
                } else {
                    FaceAnnotation mainFace = res.getFaceAnnotationsList().get(0);
                    for (int i = 1; i < res.getFaceAnnotationsList().size(); i++) {
                        if (getArea(res.getFaceAnnotations(i).getBoundingPoly().getVerticesList()) > getArea(mainFace.getBoundingPoly().getVerticesList())) {
                            mainFace = res.getFaceAnnotations(i);
                        }
                    }
                    return getMainEmotionFromFaceAnnotation(mainFace);
                }
            } else {
                return EnumEmotion.UNKNOWN;
            }
        }
        return EnumEmotion.ERROR;
    }

    public EnumEmotion getEmotion(javafx.scene.image.Image img){
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(img, null);
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", s);
            byte[] res = s.toByteArray();
            s.close();
            return getEmotion(res);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return EnumEmotion.ERROR;
    }

}
