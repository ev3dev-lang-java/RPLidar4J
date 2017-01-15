import java.io.*;
import java.net.URL;
 
public class PythonTest1{
	
	public URL path = this.getClass().getClassLoader().getResource("LIDARDummy.py"); 
	
	/**
     * Export a resource embedded into a Jar file to the local file path.
     *
     * @param resourceName ie.: "/SmartLibrary.dll"
     * @return The path to the exported resource
     * @throws Exception
     */
    static public String ExportResource(String resourceName) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        try {
            stream = PythonTest1.class.getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if(stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            jarFolder = new File(PythonTest1.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace('\\', '/');
            resStreamOut = new FileOutputStream(jarFolder + resourceName);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }

        return jarFolder + resourceName;
    }
	
	public static void main(String a[]){
		try{
			PythonTest1 pt1 = new PythonTest1();
			
			//String program = "python LidarPrint2.py /dev/ttyUSB0";
			//String program = "./python/LIDARDummy.py";
			System.out.println(pt1.path);
			String fullPath = ExportResource("/LIDARDummy.py");
			//fullPath = ExportResource("/RPLidar.sh");
			System.out.println(fullPath);
			Process p = Runtime.getRuntime().exec("python " + fullPath);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			StringBuffer output = new StringBuffer();
			String line = "";
			int i = 0;
			while ((line = in.readLine())!= null) {
				System.out.println(line);
				output.append(line + "\n");
				i++;
				if(i > 10){
					break;
				}
			}
			in.close();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}