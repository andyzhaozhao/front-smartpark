package com.iandtop.front.smartpark.door.timetask;

import com.iandtop.common.utils.DateUtils;
import com.iandtop.front.smartpark.door.dao.DoorSQLiteDao;
import com.iandtop.front.smartpark.door.util.DoorUtils;
import com.iandtop.front.smartpark.door.util.VOUtils;
import com.iandtop.front.smartpark.door.vo.DeviceVO;
import com.iandtop.front.smartpark.door.vo.QPAVO;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 会议室门禁权限定时权限同步
 * 增量同步
 *
 * @author andyzhaozhao
 */
public class ConferenceDoorTimeTask implements ITimeTask {

    @Override
    public void run(Vertx vertx, String uapServerURL, String front_code, int uapServerPort, Handler<Boolean> endHandler) {
        //会议室权限处理
        //1 获得本地缓存权限
        DoorSQLiteDao.getQPAs(vertx,"qpa", list -> {
            List<QPAVO> expiredVOs = new ArrayList<>();//将要删除的权限信息
           // List<QPAVO> syncVOs = new ArrayList<>();//将要下发到设备的权限
            for (Object o : list) {
                try {
                    QPAVO vo = QPAVO.class.newInstance();
                    BeanUtils.populate(vo, (Map<String, ? extends Object>) o);

                    Date now = DateUtils.now();
                    Date inTime = DateUtils.parseDatetime(vo.getCard_ineffectived_ts());
                    if (DateUtils.isAfter(now, inTime)) {//如果已经过期
                        expiredVOs.add(vo);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            //从缓存删除过期的权限
            DoorSQLiteDao.deleteQPA(vertx, "qpa",expiredVOs, num -> {
                //获得要删除的device
                List<DeviceVO> deleteDeviceVOs = VOUtils.qpavoToDeviceVO(expiredVOs);
                //从设备删除失效权限
                DoorUtils.deletePsnCardAuthBatchUn(vertx, deleteDeviceVOs, delResult -> {
                    //2 获得新的会议室权限
                    DoorUtils.getAuthFromUap(vertx, uapServerURL, uapServerPort, "/ichannel/jsonrpc/mobile/DLCA?front_code=" + front_code, qpavos -> {
                        if (qpavos == null || qpavos.size() <= 0) {
                            endHandler.handle(true);
                            System.out.println("无同步的会议权限");
                        } else {
                            //权限缓存到本地
                            DoorSQLiteDao.insertQPABatch(vertx, "qpa",qpavos, newNums -> {
                                List<DeviceVO> deviceVOs = VOUtils.qpavoToDeviceVO(qpavos);
                                //将新增权限写入非排序区
                                DoorUtils.writePsnCardAuthBatchUn(vertx, deviceVOs, e -> {
                                    List<String> successPKs = VOUtils.getPKList(qpavos);
                                    DoorUtils.postDownLoadAuthFromUapSuccess(vertx, uapServerURL, uapServerPort, "/ichannel/jsonrpc/mobile/UCS",
                                            successPKs,syncR->{
                                                System.out.println("同步会议预定权限成功");
                                                endHandler.handle(e);
                                            });
                                });
                            });//end of 缓存权限
                        }
                    });//end of 下载权限
                });//end of 删除设备中失效权限
            });//end of 删除过期的权限

        });
    }
}


