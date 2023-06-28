package samurayrus.vk_widget_servers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Comparator;

/**
 * Форма для обработки. Данные одного сервера с реализацией фильтра.
 * Тут некоторые строки не замоканы, т.ч аккуратнее
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "maxPlayers",
        "ip",
        "port",
        "online",
        "lastUpdate",
})
public class ServerObj {

    @JsonProperty("name")
    private String name;

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

    @JsonProperty("online")
    public void setOnline(Integer online) {
        this.online = online;
    }

    @JsonProperty("maxPlayers")
    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    //Заглушка
    //@JsonProperty("Official")
    public void setOfficial(Integer Official) {
        this.Official = Official;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("online")
    public Integer getOnline() {
        return online;
    }

    @JsonProperty("maxPlayers")
    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    // Заглушка
    // @JsonProperty("Official") 
    public Integer getOfficial() {
        if (ip.equals(ServerManager.getOfficialServerIp()) || ip.equals("ServerList is Empty")) return 1;
        else return 0;
    }


    public static final Comparator<ServerObj> COMPARE_BY_COUNT = new Comparator<ServerObj>() {
        //Сортировка. Игроки 1 серв - игроки 2 серв + значение офф/неофф. В итоге офф всегда будут на вершине.
        //Макс игроков по плану 2к, т.ч 10к более чем надо.
        @Override
        public int compare(ServerObj lhs, ServerObj rhs) {
            return rhs.getOnline() - lhs.getOnline() + rhs.getOfficial() * 10000 - lhs.getOfficial() * 10000;
        }
    };

    @Override
    public String toString() {
        return "ServerObj{" +
                "ServerName=" + name +
                ", IP='" + getIp() + '\'' +
                ", PORT='" + getPort() + '\'' +
                ", Players='" + online + '\'' +
                ", Slots='" + maxPlayers + '\'' +
                ", Official='" + getOfficial() + '\'' +
                '}';
    }
}
