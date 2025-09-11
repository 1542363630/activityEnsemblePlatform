package welfare.system.util;

import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 通过i山大站点的跳转ticket获取cookie的爬虫
 */
@Deprecated
public class IsduTicketUtil {

    public static String fetchIsduToken(String isduTicket) throws URISyntaxException {

        //--------------------ticket入口：https://i.sdu.edu.cn/cas/proxy/login/page?forward=去向&ticket=1 --------------------//

        HttpUtil.Response<String> response = HttpUtil.connect(isduTicket)
                .method(HttpMethod.GET)
                .config().followRedirects(false).set()
                .execute();

        //--------------------获取重定向：https://i.sdu.edu.cn/index.html?casID=学号--------------------//

        URI redirectUrl = response.locationURI();
        assert redirectUrl != null;
        return UriComponentsBuilder.fromUri(redirectUrl).build().getQueryParams().getFirst("casID");
    }
}
