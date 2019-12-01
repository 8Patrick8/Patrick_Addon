package at.patrickmayr.addon;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.labymod.api.LabyModAddon;
import net.labymod.api.events.MessageSendEvent;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.Material;
import net.labymod.utils.ServerData;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import scala.util.parsing.json.JSONArray;
import scala.util.parsing.json.JSONObject;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Addon extends LabyModAddon {

    private boolean enabled = true;


    public static String GETRequest(String adress) throws IOException {
        URL urlForGetRequest = new URL(adress);
        String readLine = null;
        HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
        conection.setRequestMethod("GET");
        //conection.setRequestProperty("userId", "a1bcdef"); // set userId its a sample here
        int responseCode = conection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in .readLine()) != null) {
                response.append(readLine);
            } in .close();
            // print result
            return response.toString();
            //GetAndPost.POSTRequest(response.toString());
        } else {
            return null;
        }
    }

    @Override
    public void onEnable() {
        System.out.println("Das Patrick-AAddon wurde erfolgreich geladen!");
        this.getApi().getEventManager().registerOnJoin(new Consumer<ServerData>() {

            public void accept(final ServerData serverData) {
                if (LabyMod.getInstance().getPlayerName() != null) {
                    if(!enabled){
                        return;
                    }
                    LabyMod.getInstance().displayMessageInChat("§6" + serverData.getIp());
                }
            }

        });
        this.getApi().registerModule(new CpuModule());
        this.getApi().getEventManager().register(new MessageSendEvent() {

            public boolean onSend(final String message) {
                if(message.startsWith("+")){
                    if(message.contains("getAllNames")){
                        String spieler = message.split(" ")[1];
                        String api = "";

                        //1. Request nach id
                        try {
                            String s = GETRequest("https://api.mojang.com/users/profiles/minecraft/" + spieler);
                            if(s!= null){
                                api = s;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String id="";
                        try {
                            JsonObject jsonObject = new JsonParser().parse(api).getAsJsonObject();
                            id = (jsonObject.get("id").getAsString());
                        }catch (Exception e){

                        }

                        //System.out.println("ID: " + id);

                        //2. Request nach Namen
                        //https://api.mojang.com/user/profiles/<id>/names
                        String alleNamen_json = "";
                        try {
                            String s = GETRequest("https://api.mojang.com/user/profiles/" + id + "/names");
                            if(s!= null){
                                alleNamen_json = s;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String alleNamen = "";
                        Gson gson = new Gson();

                        Type userListType = new TypeToken<ArrayList<Person>>(){}.getType();

                        ArrayList<Person> userArray = gson.fromJson(alleNamen_json, userListType);
                        int ctr = 0;

                        for(Person user : userArray) {
                            alleNamen += user + ", ";
                            if(ctr == 0){
                                //Copy Name to Clipboard
                                StringSelection stringSelection = new StringSelection(user.toString());
                                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                                clipboard.setContents(stringSelection, null);
                            }
                            ctr++;
                        }

                        if(alleNamen == ""){
                            System.out.println("Fehler!");
                        }else {
                            LabyMod.getInstance().displayMessageInChat("Namen vom Spieler " + spieler + ": " + alleNamen);
                        }
                    }
                    return true;
                }
                return false;
            }

        });
    }

    @Override
    public void loadConfig() {
        this.enabled = this.getConfig().has("enabled") ? this.getConfig().get("enabled").getAsBoolean() : true;
    }

    @Override
    protected void fillSettings(final List<SettingsElement> list) {
        final BooleanElement booleanElement = new BooleanElement("Enabled", new ControlElement.IconData(Material.DIAMOND), new Consumer<Boolean>() {

            public void accept(final Boolean enabled) {
                Addon.this.enabled = enabled;

                Addon.this.getConfig().addProperty("enabled", enabled);
                Addon.this.saveConfig();
            }

        }, this.enabled);
        list.add(booleanElement);
    }
}
