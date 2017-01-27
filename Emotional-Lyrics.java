import java.util.*;
import java.lang.*;
import java.io.*;
import com.mpatric.mp3agic.*;
import org.json.*;
import java.net.*;
import java.nio.charset.Charset;

public class Create_Database
{
   public static void main (String[] args) throws java.lang.Exception
    {
        Create_Database playlists=new Create_Database();
        playlists.input();
    }
   public void input() throws IOException{
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please enter the path of the folder which has the music saved:");
        String s = br.readLine(); 
        try{
            createPlaylists(s);
        }
        catch(Exception e){}
    }
   public void createPlaylists(String directory) throws java.lang.Exception{    
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please enter the path of the folder where you want the playlists saved:");
        String s = br.readLine(); 
        
        File fn = new File(s + "/Joy");
        fn.mkdir();
        File kn = new File(s + "/Sadness");
        kn.mkdir();
        File ln = new File(s + "/Anger");
        ln.mkdir();    
        File sn = new File(s + "/Disgust");
        sn.mkdir();
        File tn = new File(s + "/Fear");
        tn.mkdir();
        
        File folder = new File(String.valueOf(directory));
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                addTag(listOfFiles[i].getName(), directory, s);
            } else if (listOfFiles[i].isDirectory()) {
       
            }
        }
           
    }
    public void addTag(String songname, String sou_path,String s_path) throws IOException
   {
        String path = sou_path +"/" +songname;
        Mp3File mp3file;
        ID3v1 id3v1Tag;
        ID3v2 id3v2Tag;
        String artist = "";
        
        try{
            mp3file=new Mp3File(path);
            if (mp3file.hasId3v1Tag()) {
                id3v1Tag =  mp3file.getId3v1Tag();
                artist = id3v1Tag.getArtist();
                
            } else {
                if (mp3file.hasId3v2Tag()){
                    id3v2Tag =  mp3file.getId3v2Tag();
                    artist = id3v2Tag.getArtist();
            
                }

            }
            String category = findcategory(songname,artist);
            
            mp3file.save(s_path+"/" + category +"/"+ songname);
        }
        catch(Exception e){
            mp3file=null;
        }
        
      
    }
    public String findcategory(String songname, String artist)throws IOException, JSONException
    {
          
            songname = songname.replaceAll(" ","%20");
            songname = songname.replaceAll(".mp3","");
            artist = artist.replaceAll(" ","%20");
            String u = "http://api.musixmatch.com/ws/1.1/matcher.lyrics.get?apikey=660a83c4f4793d10fbd804159b4897cb&q_artist=" + artist + "&q_track=" + songname + "&format=json&page_size=1&f_has_lyrics=1";
            JSONObject json = readJsonFromUrl(u);
            String lyrics = json.getJSONObject("message").getJSONObject("body").getJSONObject("lyrics").get("lyrics_body").toString();
            
           
            
            lyrics = lyrics.replaceAll(" ","%20");
            lyrics = lyrics.replaceAll("\n","%20");
            
            String ibm_url = "https://watson-api-explorer.mybluemix.net/tone-analyzer/api/v3/tone?version=2016-05-19&text="+ lyrics + "";
           
            JSONObject json2 = readJsonFromUrl(ibm_url);
            
             
            JSONObject document_tone  = json2.getJSONObject("document_tone");
            JSONArray tone_categories = document_tone.getJSONArray("tone_categories");
            JSONObject obj = tone_categories.getJSONObject(0);
            JSONArray tones = obj.getJSONArray("tones");
            
            double max_score = 0.0;
            String max_emo ="";
            
            for(int i = 0; i < 5; i++)
            {
               
                double t = tones.getJSONObject(i).getDouble("score");
                if (t> max_score)
                {
                    max_score = t;
                    max_emo = tones.getJSONObject(i).get("tone_name").toString();
                }
               
            }
            
            return max_emo;
    }
    
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
    InputStream is = new URL(url).openStream();
    try {
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = readAll(rd);
      JSONObject json = new JSONObject(jsonText);
      return json;
    } finally {
      is.close();
    }
  }
 

  private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }
  
    
}
