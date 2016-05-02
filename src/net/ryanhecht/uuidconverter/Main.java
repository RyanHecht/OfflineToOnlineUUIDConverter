package net.ryanhecht.uuidconverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import net.ryanhecht.MCPHotel.util.getUUID;

public class Main {
	//public static File dir = new File(Paths.get("").toAbsolutePath().toString());
	public static File dir = new File("D:\\bukkitTestServer");
	public static File usermap = new File(dir, "\\plugins\\Essentials\\usermap.csv");
	public static File euserdata = new File(dir, "\\plugins\\Essentials\\userdata");
	public static File wuserdata = new File(dir, "\\world\\playerdata");
	
	public static File NEWusermap = new File(dir, "\\plugins\\Essentials\\NEWusermap.csv");
	public static File NEWeuserdata = new File(dir, "\\plugins\\Essentials\\NEWuserdata");
	public static File NEWwuserdata = new File(dir, "\\world\\NEWplayerdata");
	
	public static void main(String[] args) throws FileNotFoundException, IOException, InvalidConfigurationException {
		System.out.println("Getting usermap data...");
		HashMap<String,String> csvdata = getUsers(usermap);
		HashMap<String,UUID> onlinedata = new HashMap<String,UUID>();
		System.out.println("Fetching online UUIDs...");
		/*for(String s : csvdata.keySet()) {
			String offuuid = csvdata.get(s);
			YamlConfiguration offuuidconfig = new YamlConfiguration();
			offuuidconfig.load(new File(euserdata, offuuid + ".yml"));
			try {
			if(offuuidconfig.getString("lastAccountName") != null) {
			if(getUUID.get(offuuidconfig.getString("lastAccountName"))==null) {
				onlinedata.put(s, UUID.fromString(csvdata.get(s)));
			}
			else {
				
				onlinedata.put(s, getUUID.get(offuuidconfig.getString("lastAccountName")));
			}
			}
			}
			catch(Exception e) {
				System.out.println(requests + " | " + s + " | " + csvdata.get(s) + " | " + offuuidconfig.getString("lastAccountName"));
				e.printStackTrace();
			}
			requests++;
			if(requests % 300 == 0) {
		try {
			System.out.println("UUID request " + requests + " complete.");
			Thread.sleep(600000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
			}
		}*/
		ArrayList<String> request = new ArrayList<String>();
		Map<String,UUID> requestreturns = new HashMap<String,UUID>();
		for(String s : csvdata.keySet()) {
			String offuuid = csvdata.get(s);
			YamlConfiguration offuuidconfig = new YamlConfiguration();
			offuuidconfig.load(new File(euserdata, offuuid + ".yml"));
			if(offuuidconfig.getString("lastAccountName") != null) {
				request.add(offuuidconfig.getString("lastAccountName"));
			}
			if(request.size()%600==0) {
				Map<String,UUID> returns = getUUID.get(request);
				request.clear();
				for(String str : returns.keySet()) {
					requestreturns.put(str, returns.get(str));
				}
			}
		}
		if(!request.isEmpty()) {
			Map<String,UUID> returns = getUUID.get(request);
			request.clear();
			for(String str : returns.keySet()) {
				requestreturns.put(str, returns.get(str));
			}
		}
		
		for(String s : csvdata.keySet()) {
			String offuuid = csvdata.get(s);
			YamlConfiguration offuuidconfig = new YamlConfiguration();
			offuuidconfig.load(new File(euserdata, offuuid + ".yml"));
			if(requestreturns.get(offuuidconfig.getString("lastAccountName"))==null) {
				onlinedata.put(s, UUID.fromString(csvdata.get(s)));
			}
			else {
				onlinedata.put(s, requestreturns.get(offuuidconfig.getString("lastAccountName")));
			}
		}
		System.out.println("Making new files...");
		if(!NEWusermap.exists()) {
			try {
				NEWusermap.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(!NEWeuserdata.exists()) {
			NEWeuserdata.mkdirs();
		}
		if(!NEWwuserdata.exists()) {
			NEWwuserdata.mkdirs();
		}
		System.out.println("Populating new Essentials userdata...");
		for(String s : csvdata.keySet()) {
			File olddata = new File(euserdata, csvdata.get(s) + ".yml");
			File newdata = new File(NEWeuserdata, onlinedata.get(s) + ".yml");
			
			if(newdata.exists()) {
				//doesnt print on dupes
				System.out.println("Duplicate found!");
				YamlConfiguration dupconfig = YamlConfiguration.loadConfiguration(newdata);
				File dupoldfile = new File(euserdata, csvdata.get(dupconfig.getString("lastAccountName").toLowerCase())+ ".yml");
				
				if(dupoldfile.lastModified() < olddata.lastModified()) {
					Random r = new Random();
					File dupnewdata = new File(NEWeuserdata, onlinedata.get(s).toString() + (r.nextInt(10)) + ".yml");
					System.out.println(dupnewdata.getName() + " name");
					dupnewdata.createNewFile();
					dupconfig.save(dupnewdata);
					newdata.delete();
				}
				
			}

				try {
					newdata.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				String offuuid = csvdata.get(s);
				YamlConfiguration offuuidconfig = YamlConfiguration.loadConfiguration(new File(euserdata, offuuid + ".yml"));
				offuuidconfig.save(newdata);
				
			
		}
		System.out.println("Making updated usermap...");
		FileWriter w = new FileWriter(NEWusermap);
		for(String s : csvdata.keySet()) {
		w.append(s);
		w.append(',');
		w.append(onlinedata.get(s).toString());
		w.append('\n');
		}
		w.close();
		System.out.println("Done. TODO: world playerdata");
	
		HashMap<String,String> offonmap = new HashMap<String,String>();
		for(String s : csvdata.keySet()) {
			offonmap.put(csvdata.get(s), onlinedata.get(s).toString());
		}
		for(File f : wuserdata.listFiles()) {
			if(offonmap.get(f.getName()) != null) {
			f.renameTo(new File(NEWwuserdata, offonmap.get(f.getName())));
			}
		}
	
	}
	
	
	
	
	
	public static HashMap<String,String> getUsers(File csv) {
		BufferedReader br = null;
		HashMap<String,String> csvdata = new HashMap<String,String>();
		try {
			br = new BufferedReader(new FileReader(csv));
			String line="";
			while((line = br.readLine()) != null) {
				String[] stuff = line.split(",");
				csvdata.put(stuff[0], stuff[1]);
				
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	return csvdata;
	}
}