package org.iotacontrolcenter.ui.proxy.http;

import org.apache.http.HttpStatus;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyDto;
import org.iotacontrolcenter.dto.IccrPropertyListDto;
import org.iotacontrolcenter.dto.SimpleResponse;
import org.iotacontrolcenter.ui.properties.source.PropertySource;
import org.iotacontrolcenter.ui.proxy.BadResponseException;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;

import javax.swing.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class HttpProxy {

    private static final int MAX_TOTAL_CONN = 20;
    private static final int MAX_TOTAL_CONN_PER_ROUTE = 20;

    private ResteasyClient client;
    private PoolingHttpClientConnectionManager connectionManager;
    private ApacheHttpClient4Engine engine;
    private CloseableHttpClient httpClient;
    private String path;
    private IccrService proxy;
    private Properties serverProps;
    private ResteasyWebTarget target;

    public HttpProxy(Properties serverProps) {
        this.serverProps = serverProps;
        setUp();
    }

    public IccrPropertyListDto iccrGetConfig() throws BadResponseException {
        System.out.println("iccrGetConfig...");
        IccrPropertyListDto dto = null;
        Response response = null;
        try {
            response = proxy.getConfigProperties();

            System.out.println("response status: " + response.getStatus());

            if(response.getStatus() == HttpStatus.SC_OK) {
                dto = response.readEntity(IccrPropertyListDto.class);
            }
            else {
                throw new BadResponseException("iccrGetConfigError",
                        response.readEntity(SimpleResponse.class));
            }

        }
        catch(BadResponseException bre) {
            throw bre;
        }
        catch(Exception e) {
            System.out.println("iccrGetConfig exception: ");
            e.printStackTrace();

            Throwable cause = e.getCause();
            String msg;
            if(cause != null) {
                msg = cause.getLocalizedMessage();
            }
            else {
                msg = e.getLocalizedMessage();
            }
            throw new BadResponseException("iccrGetConfigError",
                    new SimpleResponse(false, msg));
        }
        finally {
            if(response != null) {
                response.close();
            }
        }
        return dto;
    }

    public List<SimpleResponse> iccrSetConfig(Properties props) throws BadResponseException {
        System.out.println("iccrSetConfig...");

        List<SimpleResponse> dtos = new ArrayList<>();

        for(Object o : props.keySet()) {
            String key = (String)o;
            String val = props.getProperty(key);

            System.out.println("setting " + key + " -> " + val);

            Response response =  null;
            SimpleResponse dto = null;
            try {
                response = proxy.updateConfigProperty(key, new IccrPropertyDto(key, val));

                System.out.println("response status: " + response.getStatus());

                if(response.getStatus() != HttpStatus.SC_OK) {
                    throw new BadResponseException("iccrSetConfigError",
                            response.readEntity(SimpleResponse.class));
                }
                else {
                    dtos.add(response.readEntity(SimpleResponse.class));
                }
            }
            catch(BadResponseException bre) {
                throw bre;
            }
            catch(Exception e) {
                System.out.println("iccrSetConfig exception: ");
                e.printStackTrace();

                throw new BadResponseException("iccrSetConfigError",
                        new SimpleResponse(false, e.getLocalizedMessage()));
            }
            finally {
                if(response != null) {
                    response.close();
                }
            }
        }
        return dtos;
    }

    public ActionResponse doIotaAction(String action)  throws BadResponseException {
        System.out.println("doIotaAction " + action + "...");
        Response response = null;
        ActionResponse dto = null;
        try {
            response = proxy.doIotaAction(action);

            System.out.println("response status: " + response.getStatus());

            if(response.getStatus() != HttpStatus.SC_OK) {

                SimpleResponse simple = response.readEntity(SimpleResponse.class);

                System.out.println("iota action " + action + " failed: " + simple.getMsg());

                throw new BadResponseException(action + "IotaActionError",
                        simple);
            }
            dto = response.readEntity(ActionResponse.class);
        }
        catch(BadResponseException bre) {
            System.out.println("Caught BRE, rethrowing");
            throw bre;
        }
        catch(Exception e) {
            System.out.println("iotaAction" + action  + " exception: ");
            e.printStackTrace();

            throw new BadResponseException(action + "IotaActionError",
                    new SimpleResponse(false, e.getLocalizedMessage()));
        }
        finally {
            if(response != null) {
                response.close();
            }
        }
        return dto;
    }

    private String buildPath() {
        String serverUrl = "http://" + serverProps.getProperty(PropertySource.SERVER_IP_PROP) +
                ":" + serverProps.getProperty(PropertySource.SERVER_ICCR_PORT_NUM_PROP);

        return serverUrl;
    }

    private void setUp() {
        path = buildPath();

        connectionManager = new PoolingHttpClientConnectionManager();
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
        connectionManager.setMaxTotal(MAX_TOTAL_CONN);
        connectionManager.setDefaultMaxPerRoute(MAX_TOTAL_CONN_PER_ROUTE);
        engine = new ApacheHttpClient4Engine(httpClient);

        client = new ResteasyClientBuilder().httpEngine(engine).build();
        target = client.target(UriBuilder.fromPath(path));
        proxy = target.proxy(IccrService.class);
    }
}

