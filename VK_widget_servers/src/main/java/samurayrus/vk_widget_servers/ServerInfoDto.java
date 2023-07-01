package samurayrus.vk_widget_servers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

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
@Getter
@Setter
public class ServerInfoDto {
    @JsonProperty("name")
    private String name;

    @JsonProperty("online")
    private int online;

    @JsonProperty("maxPlayers")
    private int maxPlayers;

    @JsonProperty("ip")
    private String ip;

    @JsonProperty("port")
    private int port;

    @JsonProperty("lastUpdate")
    private double lastUpdate;

    private int official;

    //На текущий момент оффициальный сервер записывается в GroupLogin.properties. Если ip сервера указан в prop файле, то он назначается оффициальным
    public int getOfficial() {
        if (ip.equals(ServerManager.getOfficialServerIp())) return 1;
        else return 0;
    }

    public static final Comparator<ServerInfoDto> COMPARE_BY_COUNT = new Comparator<ServerInfoDto>() {
        //Сортировка. Игроки 1 серв - игроки 2 серв + значение офф/неофф. В итоге офф всегда будут на вершине.
        //Макс игроков по плану 2к, т.ч 10к более чем надо.
        @Override
        public int compare(ServerInfoDto lhs, ServerInfoDto rhs) {
            return rhs.getOnline() - lhs.getOnline() + rhs.getOfficial() * 10000 - lhs.getOfficial() * 10000;
        }
    };

    @Override
    public String toString() {
        return "ServerObj{" +
                "ServerName=" + getName() +
                ", IP='" + getIp() + '\'' +
                ", PORT='" + getPort() + '\'' +
                ", Players='" + getOnline() + '\'' +
                ", Slots='" + getMaxPlayers() + '\'' +
                ", Official='" + getOfficial() + '\'' +
                '}';
    }
}
