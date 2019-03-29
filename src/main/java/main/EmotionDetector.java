package main;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EmotionDetector {
    private static EmotionDetector instance;
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

    private EnumEmotion getMainEmotion(FaceAnnotation face) {
        int joyNumber = face.getJoyLikelihood().getNumber();
        int sorrowNumber = face.getSorrowLikelihood().getNumber();
        int angerNumber = face.getAngerLikelihood().getNumber();



        if (joyNumber == sorrowNumber && sorrowNumber == angerNumber)
            return EnumEmotion.UNKNOWN;

        if (joyNumber == Math.max(Math.max(joyNumber, sorrowNumber), angerNumber)){
            return EnumEmotion.JOY;
        } else if (sorrowNumber == Math.max(Math.max(joyNumber, sorrowNumber), angerNumber)){
            return EnumEmotion.SORROW;
        } else if (angerNumber == Math.max(Math.max(joyNumber, sorrowNumber), angerNumber)){
            return EnumEmotion.ANGER;
        }

        return EnumEmotion.UNKNOWN;
    }

    private EmotionDetector() {
        try {
            vision = ImageAnnotatorClient.create();
            feature = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();
        } catch (IOException e) {
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
            }
            if (res.getFaceAnnotationsList().size() == 1) {
                return getMainEmotion(res.getFaceAnnotations(0));
            } else {
                FaceAnnotation mainFace = res.getFaceAnnotationsList().get(0);
                for (int i = 1; i < res.getFaceAnnotationsList().size(); i++) {
                    if (getArea(res.getFaceAnnotations(i).getBoundingPoly().getVerticesList()) >
                            getArea(mainFace.getBoundingPoly().getVerticesList())) {
                        mainFace = res.getFaceAnnotations(i);
                    }
                }
                return getMainEmotion(mainFace);
            }
        }
        return null;
    }

}
