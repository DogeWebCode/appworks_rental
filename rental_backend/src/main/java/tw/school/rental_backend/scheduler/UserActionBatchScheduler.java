//package tw.school.rental_backend.scheduler;
//
//import lombok.extern.log4j.Log4j2;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import tw.school.rental_backend.service.UserActionService;
//
//@Component
//@Log4j2
//public class UserActionBatchScheduler {
//
//    private final UserActionService userActionService;
//
//    public UserActionBatchScheduler(UserActionService userActionService) {
//        this.userActionService = userActionService;
//    }
//
//    // 每隔 10 分鐘執行一次批次存儲
//    @Scheduled(fixedRate = 600000)
//    public void scheduleBatchSave() {
//        userActionService.batchSaveActions();
//        log.info("UserActionBatchScheduler: batch save actions");
//    }
//}
