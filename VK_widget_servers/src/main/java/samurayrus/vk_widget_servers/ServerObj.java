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
    "ServerName",
    "Linq",
    "Icon",
    "Players",
    "Slots",
    "Raiting",
    "Official"
})
public class ServerObj{

    @JsonProperty("ServerName")
    private String ServerName;
    @JsonProperty("Linq")
    private String Linq;
    @JsonProperty("Icon")
    private String Icon;
    @JsonProperty("Players")
    private Integer Players;
    @JsonProperty("Slots")
    private Integer Slots;
    @JsonProperty("Raiting")
    private Integer Raiting;
    @JsonProperty("Official")
    private Integer Official;

    @JsonProperty("ServerName")
    public void setServerName(String ServerName) {
        this.ServerName = ServerName;
    }

    @JsonProperty("Linq")
    public void setLinq(String Linq) {
        this.Linq = Linq;
    }

    @JsonProperty("Icon")
    public void setIcon(String Icon) {
        this.Icon = Icon;
    }

    @JsonProperty("Players")
    public void setPlayers(Integer Players) {
        this.Players = Players;
    }

    @JsonProperty("Slots")
    public void setSlots(Integer Slots) {
        this.Slots = Slots;
    }

    @JsonProperty("Raiting")
    public void setRaiting(Integer Raiting) {
        this.Raiting = Raiting;
    }

    @JsonProperty("Official")
    public void setOfficial(Integer Official) {
        this.Official = Official;
    }

    @JsonProperty("ServerName")
    public String getServerName() {
        return ServerName;
    }

    @JsonProperty("Linq")
    public String getLinq() {
        return Linq;
    }

    @JsonProperty("Icon")
    public String getIcon() {
        return Icon;
    }

    @JsonProperty("Players")
    public Integer getPlayers() {
        return Players;
    }

    @JsonProperty("Slots")
    public Integer getSlots() {
        return Slots;
    }

    @JsonProperty("Raiting")
    public Integer getRaiting() {
        return Raiting;
    }

    @JsonProperty("Official")
    public Integer getOfficial() {
        return Official;
    }



    
    public static final Comparator<ServerObj> COMPARE_BY_COUNT = new Comparator<ServerObj>() {
        @Override
        public int compare(ServerObj lhs, ServerObj rhs) {
            return rhs.getPlayers()- lhs.getPlayers() + rhs.getOfficial()*10000 - lhs.getOfficial()*10000;
        }
    };

    @Override
    public String toString() {
        return "ServerObj{" +
                "ServerName=" + ServerName +
                ", Linq='" + Linq + '\'' +
                ", Icon='" + Icon + '\'' +
                ", Players='" + Players + '\'' +
                ", Slots='" + Slots + '\'' +
                ", Raiting='" + Raiting + '\'' +
                ", Official='" + Official + '\'' +
                '}';
    }
}
