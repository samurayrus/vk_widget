package samurayrus.vk_widget_servers.vk;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class VkMessageBody {
    String text;
    String icon_id;
    String url;
}