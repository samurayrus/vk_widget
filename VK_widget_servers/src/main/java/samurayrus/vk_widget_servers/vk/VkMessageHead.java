package samurayrus.vk_widget_servers.vk;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class VkMessageHead {
    String text;
    String align;
}
