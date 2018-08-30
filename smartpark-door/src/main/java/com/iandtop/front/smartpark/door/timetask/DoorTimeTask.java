package com.iandtop.front.smartpark.door.timetask;

import com.iandtop.front.smartpark.door.dao.DoorSQLiteDao;
import com.iandtop.front.smartpark.door.util.DoorUtils;
import com.iandtop.front.smartpark.door.util.VOUtils;
import com.iandtop.front.smartpark.door.vo.DeviceVO;
import com.iandtop.front.smartpark.door.vo.QPAVO;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 普通门禁权限定时权限同步
 * 全量同步
 *
 * @author andyzhao
 */
public class DoorTimeTask implements ITimeTask {

    @Override
    public void run(Vertx vertx, String uapServerURL, String front_code, int uapServerPort, Handler<Boolean> endHandler) {
        //获得门禁权限
        DoorUtils.getAuthFromUap(vertx, uapServerURL, uapServerPort, "/ichannel/jsonrpc/mobile/QPABFC?front_code=" + front_code, qpavos -> {
            DoorSQLiteDao.getQPAs(vertx, "door_qpa", list -> {
                List<QPAVO> cachVOs = new ArrayList<>();
                for (Object o : list) {
                    try {
                        QPAVO vo = QPAVO.class.newInstance();
                        BeanUtils.populate(vo, (Map<String, ? extends Object>) o);
                        cachVOs.add(vo);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

                Boolean isChanged = false;
                if(cachVOs.size()==qpavos.size()){//个数相等
                    //判断数据是否相等
                    if(!qpavos.containsAll(cachVOs)){
                        isChanged = true;
                    }
                }else{//个数不等，直接全量替换
                    isChanged = true;
                }

                if(isChanged){
                    DoorSQLiteDao.deleteQPAAll(vertx,"door_qpa",dnum->{//删除原有
                        DoorSQLiteDao.insertQPABatch(vertx,"door_qpa",qpavos,inum->{//全量增加新的
                            List<DeviceVO> deviceVOs = VOUtils.qpavoToDeviceVO(qpavos);
                            DoorUtils.writePsnCardAuthBatchOrderd(vertx, deviceVOs, e -> {//写入设备
                                System.out.println("同步门禁权限成功");
                                endHandler.handle(e);
                            });
                        });
                    });
                }else{
                    endHandler.handle(true);//不用同步，直接返回true
                }
            });
        });
    }

}
