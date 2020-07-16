/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samurayrus.vk_widget_servers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Comparator;
//{"ServerName":"SkyOn_1_OFF","Linq":"https://vk.com/skymp","Icon":"club194163484","Players":150,"Slots":170,"Raiting":9,"Official":1},

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "maxPlayers",
    "ip",
    "port",
    "online",
    "lastUpdate",
})
//    "Linq",
//    "Icon",


//    "Raiting",
 //   "Official"
public class ServerObj{

    @JsonProperty("name")
    private String name;
//    @JsonProperty("Linq")
//    private String Linq;
//    @JsonProperty("Icon")
//    private String Icon;
    @JsonProperty("online")
    private Integer online;
    @JsonProperty("maxPlayers")
    private Integer maxPlayers;

    //Игнор
    @JsonProperty("ip")
    private String ip;
    @JsonProperty("port")
    private Integer port;
    @JsonProperty("lastUpdate")
    private Double lastUpdate;
    //
//    @JsonProperty("Raiting")
    private Integer Raiting;
//    @JsonProperty("Official")
    private Integer Official;
    
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }
    
    @JsonProperty("ip")
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    @JsonProperty("port")
    public void setPort(Integer port) {
        this.port = port;
    }
    
    @JsonProperty("lastUpdate")
    public void setLastUpdate(Double lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    
    @JsonProperty("ip")
    public String getIp() {
        return ip;
    }
    
    @JsonProperty("lastUpdate")
    public Double getLastUpdate() {
        return lastUpdate;
    }
    
    @JsonProperty("port")
    public Integer getPort() {
        return port;
    }

//    @JsonProperty("Linq")
//    public void setLinq(String Linq) {
//        this.Linq = Linq;
//    }
//
//    @JsonProperty("Icon")
//    public void setIcon(String Icon) {
//        this.Icon = Icon;
//    }

    @JsonProperty("online")
    public void setOnline(Integer online) {
        this.online = online;
    }

    @JsonProperty("maxPlayers")
    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

//    @JsonProperty("Raiting")
    public void setRaiting(Integer Raiting) {
        this.Raiting = Raiting;
    }
//
//    @JsonProperty("Official")
    public void setOfficial(Integer Official) {
        this.Official = Official;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

//    @JsonProperty("Linq")
//    public String getLinq() {
//        return Linq;
//    }

//    @JsonProperty("Icon")
//    public String getIcon() {
//        return Icon;
//    }

    @JsonProperty("online")
    public Integer getOnline() {
        return online;
    }

    @JsonProperty("maxPlayers")
    public Integer getMaxPlayers() {
        return maxPlayers;
    }

//    @JsonProperty("Raiting")
    public Integer getRaiting() {
        if(ip.equals("185.241.192.136")) return 10;
         else return 0;
       // return Raiting;
    }

//    @JsonProperty("Official")
    public Integer getOfficial() {
        if(ip.equals("185.241.192.136")) return 1;
         else return 0;
    }


    
    public static final Comparator<ServerObj> COMPARE_BY_COUNT = new Comparator<ServerObj>() {
        //Сортировка. Игроки 1 серв - игроки 2 серв + значение офф/неофф. В итоге офф всегда будут на вершине.
        //Макс игроков по плану 2к, т.ч 10к более чем надо.
        @Override
        public int compare(ServerObj lhs, ServerObj rhs) {
            return rhs.getOnline()- lhs.getOnline() + rhs.getOfficial()*10000 - lhs.getOfficial()*10000;
        }
    };

    @Override
    public String toString() {
        return "ServerObj{" +
                "ServerName=" + name +
                //", Linq='" + Linq + '\'' +
                //", Icon='" + Icon + '\'' +
                ", IP='" + getIp() + '\'' +
                ", Players='" + online + '\'' +
                ", Slots='" + maxPlayers + '\'' +
                ", Raiting='" + getRaiting() + '\'' +
                ", Official='" + getOfficial() + '\'' +
                '}';
    }
}
