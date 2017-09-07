package xyz.vopen.cartier.commons.httpclient;

/**
 * xyz.vopen.cartier.commons.httpclient
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 12/07/2017.
 */
public class HttpClientTest {

    public static void main (String[] args) {
        
        HttpClient client = HttpClient.getInstance();
        
        HttpResponse response = new HttpResponse();
        client.request(AbstractHttpClient.METHOD.GET, "https://www.apple.com" ,null ,response);
        
        
        if(response.getStatusCode() == HttpStatus.SC_OK) {

            System.out.println(response.getResult());
        }
    }
    
}
