package com.chinamobile.zj;

import com.chinamobile.zj.flowProcess.enums.StencilEnum;
import com.chinamobile.zj.flowProcess.service.definition.FlowDefinitionResourceService;
import com.chinamobile.zj.flowProcess.service.resource.BaseExclusiveGatewayService;
import com.chinamobile.zj.flowProcess.service.resource.BaseSequenceFlowService;
import com.chinamobile.zj.flowProcess.service.resource.BaseStartNoneEventService;
import com.chinamobile.zj.flowProcess.service.resource.BaseUserTaskService;
import com.chinamobile.zj.flowProcess.service.resource.userTask.ReviewApplicationAndAssignByCountyTieTongHdictUserTaskService;
import com.chinamobile.zj.flowProcess.service.resource.userTask.ReviewApplicationByCountyJiaKeManagerUserTaskService;
import com.chinamobile.zj.flowProcess.service.resource.userTask.ReviewOrAssignApplicationByCountyHdictUserTaskService;
import com.chinamobile.zj.flowProcess.service.resource.userTask.SubmitPreCheckApplicationUserTaskService;
import org.apache.commons.io.FileUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@MapperScan("com.chinamobile.**.mapper*")
@Configuration
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public BaseSequenceFlowService getBaseSequenceFlowService() {
        return new BaseSequenceFlowService();
    }

    @Bean
    public BaseExclusiveGatewayService getBaseExclusiveGatewayService() {
        return new BaseExclusiveGatewayService();
    }

    @Bean
    public BaseStartNoneEventService getBaseStartNoneEventService() {
        return new BaseStartNoneEventService();
    }

    @Bean("flowDefinitionServiceMap")
    public Map<String, FlowDefinitionResourceService> getFlowDefinitionServiceMapBean() {
        Map<String, FlowDefinitionResourceService> definitionResourceId2DefinitionService = new HashMap<String, FlowDefinitionResourceService>() {
            {
                String jsonXml;
                File jsonFileOfProcess = new File("C:\\work\\zj\\process-china-moblie\\src\\main\\resources\\static\\precheck_install_process.json");
                try {
                    jsonXml = FileUtils.readFileToString(jsonFileOfProcess,
                            Charset.defaultCharset());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                FlowDefinitionResourceService service1 = new FlowDefinitionResourceService(jsonXml);
                put(service1.getFlowProcessBo().getProperties().getProcessId(), service1);
            }
        };
        return definitionResourceId2DefinitionService;
    }

    @Bean("userTaskServiceMap")
    public Map<String, BaseUserTaskService> getUserTaskServiceMapBean() {
        Map<String, BaseUserTaskService> definitionKey2BaseUserTaskService = new HashMap<String, BaseUserTaskService>() {
            {
                BaseStartNoneEventService startNoneEvent = getBaseStartNoneEventService();
                put(StencilEnum.START_NONE_EVENT.getId(), startNoneEvent);

                BaseUserTaskService service1 = new SubmitPreCheckApplicationUserTaskService();
                put(service1.getDefinitionKey(), service1);

                BaseUserTaskService service3 = new ReviewApplicationAndAssignByCountyTieTongHdictUserTaskService();
                put(service3.getDefinitionKey(), service3);

                BaseUserTaskService service4 = new ReviewOrAssignApplicationByCountyHdictUserTaskService();
                put(service4.getDefinitionKey(), service4);

                BaseUserTaskService service2 = new ReviewApplicationByCountyJiaKeManagerUserTaskService();
                put(service2.getDefinitionKey(), service2);
            }
        };
        return definitionKey2BaseUserTaskService;
    }

    // todo zj 重启之后要自动加载所有bean

//    @Bean("userTaskServiceClassMap")
//    public Map<String, ? extends Class<BaseUserTaskService>> getUserTaskServiceClassMapBean() {
//        Map<String, Class> definitionKey2BaseUserTaskClassService = new HashMap<String, Class>() {
//            {
//                BaseStartNoneEventService startNoneEvent = new BaseStartNoneEventService();
//                put(startNoneEvent.getDefinitionKey(), BaseStartNoneEventService.class);
//
//                BaseUserTaskService service1 = new SubmitPreCheckApplicationUserTaskService();
//                put(service1.getDefinitionKey(), SubmitPreCheckApplicationUserTaskService.class);
//
//                BaseUserTaskService service2 = new ReviewApplicationByCountyJiaKeManagerUserTaskService();
//                put(service2.getDefinitionKey(), ReviewApplicationByCountyJiaKeManagerUserTaskService.class);
//            }
//        };
//        return definitionKey2BaseUserTaskClassService;
//    }
}
