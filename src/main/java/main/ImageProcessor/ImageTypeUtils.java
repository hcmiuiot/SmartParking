package main.ImageProcessor;

import javafx.embed.swing.SwingFXUtils;
import javafx.embed.swt.SWTFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageTypeUtils {

	public static String fxImage2Base64(Image fxImg) {
		if (fxImg == null)
			return null;
		BufferedImage bImg = SwingFXUtils.fromFXImage(fxImg, null);
		ByteArrayOutputStream s = new ByteArrayOutputStream();
		try {
			ImageIO.write(bImg, "png", s);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] res = s.toByteArray();
		return Base64.getEncoder().encodeToString(res);
	}

	public static  Image base642fxImg(String base64) {
		if (base64 == null)
			return null;
		byte[] b = Base64.getDecoder().decode(base64);
		ByteArrayInputStream is = new ByteArrayInputStream(b);
		BufferedImage bf = null;
		try {
			bf = ImageIO.read(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return SwingFXUtils.toFXImage(bf, null);
	}

}
