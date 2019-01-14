package cn.edu.neu.scenicspots.model;

import cn.edu.neu.scenicspots.datastructure.Queue;
import cn.edu.neu.scenicspots.datastructure.Stack;
import cn.edu.neu.scenicspots.datastructure.Map;
import cn.edu.neu.scenicspots.gui.UIEngine;
import org.json.JSONObject;


public class ParkingLot {
    private Queue<Car> waitingLine;
    private Stack<Car> bufferStack;
    private Stack<Car> inParking;
    private Map<String, Car> carCache;

    public ParkingLot(int size){
        waitingLine = new Queue<>();
        bufferStack = new Stack<>(size);
        inParking = new Stack<>(size);
        carCache = new Map<>();
    }

    public synchronized void come(String uniqueID){
        Car toEnter = new Car(uniqueID);
        Car check = carCache.get(uniqueID);
        if(check!=null){
            if(check.status!=Car.GONE) {
                System.out.println("车牌号重复，已有车牌号为" + uniqueID + "的车辆在停车场或等待队列中");
                UIEngine.executeJS("mdui.alert('车牌号重复，已有车牌号为"+uniqueID+"的车辆在停车场或等待队列中。');app.unlock();");
                return;
            }
        }
        carCache.put(uniqueID, toEnter);
        if(inParking.full()){
            waitingLine.add(toEnter);
            System.out.println("[等待入队] 车位已满，车辆"+uniqueID+"进入等待队列");
            UIEngine.executeJS("ParkingControl.count=0; ParkingControl.addToWaiting('"+toEnter.toString()+"');"+"app.unlock();");
        } else {
            toEnter.startParkingTime = System.currentTimeMillis();
            inParking.push(toEnter);
            System.out.println("[入库] 车位有余，车辆"+uniqueID+"进入车库");
            UIEngine.executeJS("ParkingControl.count=0; ParkingControl.addToParking('"+toEnter.toString()+"');"+"app.unlock();");
        }
    }

    public void printLength(){
        System.out.println("Waiting="+waitingLine.length());
        System.out.println("Parking="+inParking.getSize());
        System.out.println("Buffer="+bufferStack.getSize());
    }
    public synchronized void go(String uniqueID){
        Car carToFind = carCache.get(uniqueID);
        if(!(carToFind==null)){ // 无此辆车
            if(inParking.find(carToFind)!=-1){ // 有此辆车停泊
                printLength();
                carToFind.status = Car.GONE;
                Car carPointer = inParking.pop();
                StringBuilder jsToExecute = new StringBuilder("ParkingControl.removeFrom(app.parking,'"+carPointer.uniqueID+"');");

                while(!inParking.empty()&&!carPointer.uniqueID.equals(carToFind.uniqueID)){
                    System.out.println("While1==>"+carToFind.toString());
                    bufferStack.push(carPointer);
                    jsToExecute.append("ParkingControl.addToBuffer('"+carPointer.toString()+"');");
                    carPointer = inParking.pop();
                    jsToExecute.append("ParkingControl.removeFrom(app.parking,'"+carPointer.uniqueID+"');");
                }

                // 车辆离库
                System.out.println("[离库] 车辆"+carPointer.uniqueID+"离库");
                System.out.println("  全局等待时间（含等候车位和调度时间）"+(System.currentTimeMillis()-carPointer.enterTime)+"ms");
                System.out.println("  停靠时间"+(System.currentTimeMillis()-carPointer.startParkingTime)+"ms");
                jsToExecute.append("app.parkingMsg='"+
                        "车辆"+carPointer.uniqueID+"离库 "+
                        "全局等待时间（含等候车位和调度时间）"+(System.currentTimeMillis()-carPointer.enterTime)/1000.0+"s"+
                        " 费用"+(System.currentTimeMillis()-carPointer.enterTime)/10000.0+"元"+
                        "';");
                while(!bufferStack.empty()){ // remove all from buffer / add to parking
                    Car toRestore = bufferStack.pop();
                    System.out.println("While1==>"+toRestore.toString());
                    inParking.push(toRestore);
                    jsToExecute.append("ParkingControl.removeFrom(app.buffer,'"+toRestore.uniqueID+"');");
                    jsToExecute.append("ParkingControl.addToParking('"+toRestore.toString()+"');");
                    System.out.println("[让行返库] 车辆"+toRestore.uniqueID+"让行结束，返回车库");
                }

                if(waitingLine.length()>0) { // 等待的车进入一格 remove 1 from waiting / add to parking
                    Car toPoll = waitingLine.poll();
                    System.out.println("[入库] 等待中的车辆 "+toPoll.uniqueID+" 因车位有余进入停车场。");
                    toPoll.startParkingTime = System.currentTimeMillis();
                    inParking.push(toPoll);
                    jsToExecute.append("ParkingControl.removeFrom(app.waiting,'"+toPoll.uniqueID+"');");
                    jsToExecute.append("ParkingControl.addToParking('"+toPoll.toString()+"');");
                }
                UIEngine.executeJS("ParkingControl.count=0;"+jsToExecute.toString()+"app.unlock();");
                printLength();
            } else { // 车辆在等待
                System.out.println("[等待队列] Waiting car.");

            }
        }
    }

    class Car{
        String uniqueID;
        long startParkingTime;
        long enterTime;
        int status;
        static final int GONE = -1;

        Car(String uniqueID){
            this.uniqueID = uniqueID;
            this.enterTime = System.currentTimeMillis();
        }

        boolean equals(Car another){
            return another.uniqueID.equals(this.uniqueID);
        }

        @Override
        public String toString(){
            return toJSONString();
        }

        String toJSONString(){
            JSONObject json = new JSONObject();
            json.put("id",uniqueID);
            json.put("img",uniqueID.hashCode()%8);
            json.put("startParkingTime",startParkingTime);
            json.put("enterTime", enterTime);
            return UIEngine.formatForJS(json.toString());
        }

    }
}

