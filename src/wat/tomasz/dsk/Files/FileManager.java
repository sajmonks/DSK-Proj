package wat.tomasz.dsk.Files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import wat.tomasz.dsk.Utils.Utils;

public class FileManager {
	
	FileManager() {
		
	}
	
	public static int [] loadParameters() {
		List<String> lines = readText("local.txt");
		if(lines.size() == 1) {
			int [] params = new int [2];
			String [] split = lines.get(0).split(" ");
			
			if(split.length == 2) {
				params[0] = Utils.getInt(split[0]);
				params[1] = Utils.getInt(split[1]);
				return params;
			}
		}
		return null;
	}
	
	public static void writeParameters(int id, int listenPort) {
		if(!fileExists("local.txt")) {
			createFile("local.txt");
			writeText("local.txt", "" + id + " " + listenPort, StandardOpenOption.CREATE_NEW);
		}

	}
	
	public static void removeIfExists(String filePath) {
		File file = new File(filePath);
		if(file.exists()) {
			file.delete();
		}
	}
	
	public static boolean fileExists(String path) {
		if(new File(path).exists())
			return true;
		
		return false;
	}
	
	public static void createFile(String path) {
		if(!fileExists(path)) {
			try {
				new File(path).createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static List<String> readText(String file) {
		try {
			return Files.readAllLines(Paths.get(file) ); 
		} catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void writeText(String file, String message, StandardOpenOption option) {
		if(!fileExists(file)) {
			System.out.println("Nie mo¿na znalezc pliku: " + file);
			return;
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			writer.write(message);
			writer.close();
			//Files.write(Paths.get(file), message.getBytes(), option);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
