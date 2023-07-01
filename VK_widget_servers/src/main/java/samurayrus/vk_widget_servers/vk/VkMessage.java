package samurayrus.vk_widget_servers.vk;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class VkMessage {
    VkMessageHead[] head;
    int title_counter;
    String title;
    String title_url;
    VkMessageBody[][] body;
}