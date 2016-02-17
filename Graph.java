package ru.polynkina.irina.graphs;

import java.util.Map;

public class Graph {

    final static double UNINITIALIZED_VALUE = -1;
    final static double STANDARD_TIME_IN_DAY = 8;
    final static double FLOAT_TIME_IN_DAY = 7.2;
    final static char CHAR_DESIGNATION_WEEKEND = 'f';
    final static char CHAR_DESIGNATION_DAY = 'd';
    final static char CHAR_DESIGNATION_NIGHT = 'n';
    final static char CHAR_DESIGNATION_UNIVERSAL_DAY = 'u';

    final static int CODE_SHORT_DAY = 0;
    final static int CODE_HOLIDAY = 1;
    final static int CODE_DAY_OFF = 2;
    final static double ACCEPTABLE_ACCURACY = 1.0e-10;
    final static double NEGLIGIBLE_TIME_INTERVAL = 0.001;

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

    public void printWorkTime(int AMOUNT_OF_DAYS){
        System.out.print(name + ": \t");
        for(int indexDay = 0; indexDay < AMOUNT_OF_DAYS; ++indexDay){
            System.out.print(workTime[indexDay] + " ");
        }
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
        try{
            if(indexDay >= workTime.length) throw new Exception("Попытка выхода за пределы массива");
        }catch (Exception exc){
            exc.printStackTrace();
            System.exit(0);
        }
        workTime[indexDay] = time;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getRule(){
        return rule;
    }

    public int getLengthRule(){
        return rule.length();
    }

    public char getRuleOfDay(int positionForRule){
        try{
            if(positionForRule >= getLengthRule()) throw new Exception("Попытка выхода за пределы правила");
        }catch (Exception exc){
            exc.printStackTrace();
            System.exit(0);
        }

        return rule.charAt(positionForRule);
    }

    public double getDaytime(){
        return daytime;
    }

    public double getUniqueTime(){
        return getDaytime();
    }

    public double getNightTime(){
        return getDaytime();
    }

    public String getDaytimeSign(){
        return daytimeSign;
    }

    public String getUniqueTimeSign(){
        return getDaytimeSign();
    }

    public String getNightTimeSign(){
        return getDaytimeSign();
    }

    public int getCounter(){
        return counter;
    }

    public double getWorkTimeInMonth(){
        return workTimeInMonth;
    }

    public double getWorkTime(int indexDay){
        try{
            if(indexDay >= workTime.length) throw new Exception("Попытка выхода за пределы массива");
        }catch (Exception exc){
            exc.printStackTrace();
            System.exit(0);
        }
        return workTime[indexDay];
    }


    /*******************************************************************************************************************************************
                                                        private methods
     ******************************************************************************************************************************************/


    private void fillUninitializedWorkingTime(int spreadValue, int amountSpreadValue, int rareValue, int amountRareValue, double frequency, final int AMOUNT_OF_DAYS){
        int lengthRule = getLengthRule();
        int positionForRule = getCounter();

        double currentFrequency = 0;
        int counterSpreadTime = 0;
        int counterRareTime = 0;

        for(int indexDay = 0; indexDay < AMOUNT_OF_DAYS; ++indexDay){
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
            if(++positionForRule == lengthRule) positionForRule = 0;
        }
    }


    /*******************************************************************************************************************************************
                                                        public methods
     ******************************************************************************************************************************************/


    public int getAmountDaysWithMinTime(int minWorkTime, int maxWorkTime, double sumWorkTime, final int AMOUNT_OF_DAYS){
        int daysWithMinTime;
        for(daysWithMinTime = 0; daysWithMinTime <= AMOUNT_OF_DAYS; ++daysWithMinTime){
            int daysWithMaxTime = AMOUNT_OF_DAYS - daysWithMinTime;
            if(daysWithMinTime * minWorkTime + daysWithMaxTime * maxWorkTime - sumWorkTime < ACCEPTABLE_ACCURACY) return daysWithMinTime;
        }
        return daysWithMinTime;
    }



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



    public void createUninitializedWorkTimeArray(final int AMOUNT_OF_DAYS){
        workTime = new double[AMOUNT_OF_DAYS];
        for(int i = 0; i < AMOUNT_OF_DAYS; ++i){
            setWorkTime(i, UNINITIALIZED_VALUE);
        }
    }



    public void setWeekend(final int AMOUNT_OF_DAYS){
        int lengthRule = getLengthRule();
        int positionForRule = getCounter();

        for(int indexDay = 0; indexDay < AMOUNT_OF_DAYS; ++indexDay){
            if(getRuleOfDay(positionForRule) == CHAR_DESIGNATION_WEEKEND){
                setWorkTime(indexDay, 0);
            }
            if(++positionForRule == lengthRule) positionForRule = 0;
        }
    }



    public void setShortDayAndHolidays(final int AMOUNT_OF_DAYS, final Map<Integer, Integer> shortDayAndHolidays){
        int lengthRule = getLengthRule();
        int positionForRule = getCounter();

        for(int indexDay = 0; indexDay < AMOUNT_OF_DAYS; ++indexDay){
            if(getRuleOfDay(positionForRule) != CHAR_DESIGNATION_WEEKEND){
                for(Map.Entry<Integer, Integer> container : shortDayAndHolidays.entrySet()){
                    if(container.getKey() == indexDay + 1){
                        if(container.getValue() == CODE_SHORT_DAY) setWorkTime(indexDay, getDaytime() - 1);
                        else if(container.getValue() == CODE_HOLIDAY) setWorkTime(indexDay, getDaytime());
                    }
                }
            }
            if(++positionForRule == lengthRule) positionForRule = 0;
        }
    }



    public int getAmountUninitializedDays(final int AMOUNT_OF_DAYS){
        int amountUninitializedDays = 0;
        for(int indexDay = 0; indexDay < AMOUNT_OF_DAYS; ++indexDay){
            if(getWorkTime(indexDay) == UNINITIALIZED_VALUE) ++amountUninitializedDays;
        }
        return amountUninitializedDays;
    }



    public double getSumTimeInitializedDays(final int AMOUNT_OF_DAYS){
        double sumTime = 0;
        for(int indexDay = 0; indexDay < AMOUNT_OF_DAYS; ++indexDay){
            if(getWorkTime(indexDay) != UNINITIALIZED_VALUE) sumTime += getWorkTime(indexDay);
        }
        return sumTime;
    }



    public void generateGraph(final int AMOUNT_OF_DAYS, int amountUninitializedDays, double sumTimesUninitializedDays){
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
        fillUninitializedWorkingTime(spreadValue, amountSpreadValue, rareValue, amountRareValue, frequency, AMOUNT_OF_DAYS);
    }



    public double getSumWorkTime(final int AMOUNT_OF_DAYS){
        double sumWorkTime = 0;
        for(int indexDay = 0; indexDay < AMOUNT_OF_DAYS; ++indexDay) {
            sumWorkTime += getWorkTime(indexDay);
        }
        return sumWorkTime;
    }
}