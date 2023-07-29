package jnu.cross.config;

import com.citahub.cita.protocol.CITAj;

/**
 * @author SDKany
 * @ClassName CITAConfig
 * @Date 2023/7/29 15:15
 * @Version V1.0
 * @Description
 */
public class CITAConfig {

    public static String citaURL = "http://10.154.24.5:1337";
    public static CITAj citaj = CITAj.build(new com.citahub.cita.protocol.http.HttpService(citaURL));
}
