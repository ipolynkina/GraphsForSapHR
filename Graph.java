package ru.polynkina.irina.graphs;

import java.util.Map;

public class Graph {

    final static double UNINITIALIZED_VALUE = -1;
    final static double STANDARD_TIME_IN_DAY = 8;
    final static double FLOAT_TIME_IN_DAY = 7.2;
    final static char TYPE_DESIGNATION_WEEKEND = 'f';
    final static char TYPE_DESIGNATION_DAY = 'd';
    final static char TYPE_DESIGNATION_NIGHT = 'n';
    final static char TYPE_DESIGNATION_UNIVERSAL_DAY = 'u';

    final static int CODE_SHORT_DAY = 0;
    final static int CODE_HOLIDAY = 1;
    final static int CODE_DAY_OFF = 2;
    final static double ACCEPTABLE_ACCURACY = 1.0e-10;
    final static double ACCEPTABLE_ACCURACY_TO_TIME = 0.001;

    private int id;
    private String name;
    private String rule;
    private double daytime;
    private String daytimeSign;
    private int counter;
    private double workTimeInMonth;
    private double workTime[];

    Graph(int id, String name, String rule, double daytime, String daytimeSign, double workTimeInMonth){
        this.id = id;
        this.name = name;
        this.rule = rule;
        this.daytime = daytime;
        this.daytimeSign = daytimeSign;
        this.workTimeInMonth = workTimeInMonth;
    }

    public void printInfo(){
        System.out.print("id: " + id + "\tname: " + name + "\trule: " + rule + "\tdaytime: " + daytime);
        System.out.println("\tdaytimeSign: " + daytimeSign + "\tworkTimeInMonth: " + workTimeInMonth + "\tcounter: " + counter);
    }

    public void printWorkTime(int amountDay){
        System.out.print(name + ": \t");
        for(int indexDay = 0; indexDay < amountDay; ++indexDay) System.out.print(workTime[indexDay] + " ");
        System.out.println();
    }


    /*******************************************************************************************************************************************
                                                        getters and setters
     ******************************************************************************************************************************************/


    public void setCounter(int counter){
        this.counter = counter;
    }

    public void setWorkTimeInMonth(double workTimeInMonth){
        this.workTimeInMonth = workTimeInMonth;
    }

    public void setWorkTime(int indexDay, double time){
        workTime[indexDay] = time;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public int getLengthRule(){
        return rule.length();
    }

    public char getRuleOfDay(int indexDay){
        return rule.charAt(indexDay);
    }

    public double getDaytime(){
        return daytime;
    }

    public String getDaytimeSign(){
        return daytimeSign;
    }

    public String getUniqueTimeSign(){
        return getDaytimeSign();
    }

    public int getCounter(){
        return counter;
    }

    public double getWorkTimeInMonth(){
        return workTimeInMonth;
    }

    public double getWorkTime(int indexDay){
        return workTime[indexDay];
    }


    /*******************************************************************************************************************************************
                                                        private methods
     ******************************************************************************************************************************************/


    private int getAmountDaysWithMinTime(int minWorkTime, int maxWorkTime, double sumWorkTime, int amountDay){
        int daysWithMinTime;
        for(daysWithMinTime = 0; daysWithMinTime <= amountDay; ++daysWithMinTime){
            int daysWithMaxTime = amountDay - daysWithMinTime;
            if(daysWithMinTime * minWorkTime + daysWithMaxTime * maxWorkTime - sumWorkTime < ACCEPTABLE_ACCURACY) return daysWithMinTime;
        }
        return daysWithMinTime;
    }



    private void fillUninitializedWorkingTime(int spreadValue, int amountSpreadValue, int rareValue, int amountRareValue, double frequency, int amountDay){
        int lengthRule = getLengthRule();
        int currentCounter = getCounter();

        double currentFrequency = 0;
        int counterSpreadTime = 0;
        int counterRareTime = 0;

        for(int indexDay = 0; indexDay < amountDay; ++indexDay){
            if(getWorkTime(indexDay) == UNINITIALIZED_VALUE){
                if(currentFrequency < frequency && counterSpreadTime < amountSpreadValue || counterRareTime == amountRareValue){
                    setWorkTime(indexDay, spreadValue);
                    ++counterSpreadTime;
                    ++currentFrequency;
                }else{
                    setWorkTime(indexDay, rareValue);
                    ++counterRareTime;
                    currentFrequency = 0;
                }
            }
            if(++currentCounter == lengthRule) currentCounter = 0;
        }
    }


    /*******************************************************************************************************************************************
     public methods
     ******************************************************************************************************************************************/


    public double calculateFrequency(int amountDaysWithMinTime, int amountDaysWithMaxTime){
        double frequency;
        if(amountDaysWithMinTime > amountDaysWithMaxTime){
            if(amountDaysWithMaxTime != 0) frequency = (double) amountDaysWithMinTime / amountDaysWithMaxTime;
            else frequency = amountDaysWithMinTime;
        } else{
            if(amountDaysWithMinTime != 0) frequency = (double) amountDaysWithMaxTime / amountDaysWithMinTime;
            else frequency = amountDaysWithMaxTime;
        }
        return frequency;
    }



    public void createUninitializedWorkTimeArray(int amountDay){
        workTime = new double[amountDay];
        for(int i = 0; i < amountDay; ++i) setWorkTime(i, UNINITIALIZED_VALUE);
    }



    public void setWeekend(int amountDay){
        int lengthRule = getLengthRule();
        int currentCounter = getCounter();

        for(int indexDay = 0; indexDay < amountDay; ++indexDay){
            if(getRuleOfDay(currentCounter) == TYPE_DESIGNATION_WEEKEND) setWorkTime(indexDay, 0);
            if(++currentCounter == lengthRule) currentCounter = 0;
        }
    }



    public void setShortDayAndHolidays(int amountDay, Map<Integer, Integer> shortDayAndHolidays){
        int lengthRule = getLengthRule();
        int currentCounter = getCounter();

        for(int indexDay = 0; indexDay < amountDay; ++indexDay){
            if(getRuleOfDay(currentCounter) != TYPE_DESIGNATION_WEEKEND){
                for(Map.Entry<Integer, Integer> container : shortDayAndHolidays.entrySet()){
                    if(container.getKey() == indexDay + 1){
                        if(container.getValue() == CODE_SHORT_DAY) setWorkTime(indexDay, getDaytime() - 1);
                        else if(container.getValue() == CODE_HOLIDAY) setWorkTime(indexDay, getDaytime());
                    }
                }
            }
            if(++currentCounter == lengthRule) currentCounter = 0;
        }
    }



    public int getAmountUninitializedDays(int amountDay){
        int amountUninitializedDays = 0;
        for(int indexDay = 0; indexDay < amountDay; ++indexDay){
            if(getWorkTime(indexDay) == UNINITIALIZED_VALUE) ++amountUninitializedDays;
        }
        return amountUninitializedDays;
    }



    public double getSumTimeInitializedDays(int amountDay){
        double sumTime = 0;
        for(int indexDay = 0; indexDay < amountDay; ++indexDay){
            if(getWorkTime(indexDay) != UNINITIALIZED_VALUE) sumTime += getWorkTime(indexDay);
        }
        return sumTime;
    }



    public void generateGraph(int amountDay, int amountUninitializedDays, double sumTimesUninitializedDays){
        double averageWorkTime;
        if(amountUninitializedDays != 0) averageWorkTime = sumTimesUninitializedDays / amountUninitializedDays;
        else averageWorkTime = sumTimesUninitializedDays;

        int minWorkTime = (int) averageWorkTime;
        int maxWorkTime = minWorkTime > averageWorkTime ? minWorkTime - 1 : minWorkTime + 1;

        int amountDaysWithMinTime = getAmountDaysWithMinTime(minWorkTime, maxWorkTime, sumTimesUninitializedDays, amountUninitializedDays);
        int amountDaysWithMaxTime = amountUninitializedDays - amountDaysWithMinTime;

        double frequency = calculateFrequency(amountDaysWithMinTime, amountDaysWithMaxTime);
        int spreadValue = amountDaysWithMinTime >= amountDaysWithMaxTime ? minWorkTime : maxWorkTime;
        int amountSpreadValue = amountDaysWithMinTime >= amountDaysWithMaxTime ? amountDaysWithMinTime : amountDaysWithMaxTime;
        int rareValue = amountDaysWithMinTime < amountDaysWithMaxTime ? minWorkTime : maxWorkTime;
        int amountRareValue = amountDaysWithMinTime < amountDaysWithMaxTime ? amountDaysWithMinTime : amountDaysWithMaxTime;
        fillUninitializedWorkingTime(spreadValue, amountSpreadValue, rareValue, amountRareValue, frequency, amountDay);
    }



    public double getSumWorkTime(int amountDay){
        double sumWorkTime = 0;
        for(int indexDay = 0; indexDay < amountDay; ++indexDay) {
            sumWorkTime += getWorkTime(indexDay);
        }
        return sumWorkTime;
    }
}