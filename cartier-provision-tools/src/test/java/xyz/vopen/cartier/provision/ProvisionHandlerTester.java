package xyz.vopen.cartier.provision;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import xyz.vopen.cartier.provision.exception.CertException;
import xyz.vopen.cartier.provision.exception.RequestException;
import xyz.vopen.cartier.provision.ext.Result;
import xyz.vopen.cartier.provision.ext.response.GetTeamsResponse;
import xyz.vopen.cartier.provision.ext.response.ListCertResponse;
import xyz.vopen.cartier.provision.ext.response.SubmitCertificateResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Semaphore;

/**
 * xyz.vopen.cartier.provision
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 14/07/2017.
 */
public class ProvisionHandlerTester {


    @Test
    public void testLogin () throws Exception, RequestException, CertException {
        ProvisionProcessor.ProvisionHandler handler = ProvisionProcessor.getInstance().getHandler();

        Result result = handler.login("MingjunLee34@163.com", "Member0524");

        System.out.println("----------------");
        result.print();
        System.out.println("----------------");


        GetTeamsResponse result1 = handler.getTeams();
        System.out.println("----------------");
        result1.print();
        String teamId = result1.getTeams().get(0).getTeamId();
        System.out.println("----------------");

        ListCertResponse response = handler.listCertRequests(teamId, null);
        if(response.getTotalRecords() >= 2) {
            System.out.println("@ 开发者证书数量已经超过限制!最多2个!");
            return ;
        }
        
        response.print();
        System.out.println("----------------");
        
        try {

            SubmitCertificateResponse submitCertificateResponse = handler.submitCertificateRequest(teamId, "/Users/ive/Documents/pyw-dev/cartier-all-in-one/env/CSR.certSigningRequest");
            if (submitCertificateResponse != null) {
                submitCertificateResponse.print();
                System.out.println("----------------");
                String id = submitCertificateResponse.getCertRequest().getCertificateId();
                System.out.println(id);

                if (StringUtils.isNoneBlank(id)) {

                    String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".cer";
                    handler.downloadCertificateContent(teamId, id, "/Users/ive/Documents/pyw-dev/cartier-all-in-one/env/" + fileName);

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        System.out.println("------------------");
//        ListAppIdsResponse appIdsResponse = handler.listAppIds(teamId);
//        appIdsResponse.print();

    }

    @Test
    public void testHandlerThreadLocal () throws Exception {

        final Semaphore semaphore = new Semaphore(2);

        new Thread(new Runnable() {
            @Override
            public void run () {
                try {
                    semaphore.acquire(1);
                    for (int i = 0; i < 4; i++) {

                        ProvisionProcessor.ProvisionHandler handler = ProvisionProcessor.getInstance().getHandler();
                        System.out.println(Thread.currentThread().getName() + " - " + handler);
                        Thread.sleep(2000);

                    }
                    semaphore.release(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run () {
                try {
                    semaphore.acquire(1);
                    for (int i = 0; i < 3; i++) {
                        ProvisionProcessor.ProvisionHandler handler = ProvisionProcessor.getInstance().getHandler();
                        System.out.println(Thread.currentThread().getName() + " - " + handler);
                        Thread.sleep(2000);

                    }
                    semaphore.release(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        semaphore.acquire(2);

    }

}
