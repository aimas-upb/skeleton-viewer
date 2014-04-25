package amilab.skeleton_viewer.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;

import sun.misc.BASE64Decoder;
import amilab.skeleton_viewer.shared.ImageRgb;
import amilab.skeleton_viewer.shared.Skeleton3D;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FileLoader {
	
	private static final int SKELETON_TRESHOLD_MAX = 100 * 1024;
	List<Skeleton3D> skeletons = new ArrayList<Skeleton3D>();
	List<ImageRgb> images = new ArrayList<ImageRgb>();
	
	private JsonParser parser = new JsonParser();
	private Gson gson = new Gson();
	private BASE64Decoder decoder = new BASE64Decoder();
	
	public static void main(String[] args) throws IOException {
		new FileLoader().loadFromFile("./data", "s+rgb.txt");
	}
	
	public void loadFromFile(String baseFolder, String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(baseFolder, fileName)), 4 * 1024 * 1024);
		try {
			int lineNumber = 0;
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				lineNumber ++;
				processLine(lineNumber, line);
			}
		} finally {
			IOUtils.closeQuietly(reader);
		}
		
		Collections.sort(skeletons, Skeleton3D.COMPARATOR_BY_TIME);
		Collections.sort(images, ImageRgb.COMPARATOR_BY_TIME);
		
	}
	
	public void awaitLoading() {
		return;
	}
	
	private void processLine(int lineNumber, String line) {
		if (line.length() < SKELETON_TRESHOLD_MAX) {
			line = line.replaceAll("\"X\"", "\"x\"");
			line = line.replaceAll("\"Y\"", "\"y\"");
			line = line.replaceAll("\"Z\"", "\"z\"");
		}
		
		JsonObject object = (JsonObject) parser.parse(line);
		
		String type = object.get("type").getAsString();
		
		if (type.equals("image_rgb")) {
			loadImageRgb(object);
		} else if (type.equals("skeleton")) {
			loadSkeleton(lineNumber, object);
		}
	}


	private void loadSkeleton(int lineNumber, JsonObject object) {
		JsonObject o3D = object.getAsJsonObject("skeleton_3D"); 
		Skeleton3D s = gson.fromJson(o3D, Skeleton3D.class);
		
		s.time = object.get("created_at").getAsLong();
		s.sensorId = object.get("sensor_id").getAsString();
		s.lineNumber = lineNumber;
		skeletons.add(s);
	}


	private void loadImageRgb(JsonObject object) {
		ImageRgb r = gson.fromJson(object, ImageRgb.class);		
		images.add(r);
		
		saveRgbToFile(r, object);
	}
	
	
	private void saveRgbToFile(ImageRgb r, JsonObject object) {
		try {
			File folder = new File("image-rgb/" + r.sensor_id);
			folder.mkdirs();
			
			File file = new File(folder, String.format("%d.jpg", r.created_at));
			
			System.out.println("Saving image to " + file.getAbsolutePath());
			
			BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file), 750 * 1024);
			try {
				String imageData = ((JsonObject)object.get("image_rgb")).get("image").getAsString(); 
				os.write(decoder.decodeBuffer(imageData));
			} finally {
				IOUtils.closeQuietly(os);
			}			
		} catch (Exception e) {
			System.err.println("Got error saving rgb: " + e);
		}
		
		
	}
	
	
	
}
