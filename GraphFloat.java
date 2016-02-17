package ru.polynkina.irina.graphs;

public class GraphFloat extends Graph {

    public static double MINUTE_FOR_FLOAT_TIME = 0.2;

    GraphFloat(int id, String name, String rule, double daytime, String daytimeSign, double workTimeInMonth){
        super(id, name, rule, daytime, daytimeSign, workTimeInMonth);
        setNormTimeForStandardGraphs(workTimeInMonth);
    }


    private void setNormTimeForStandardGraphs(double standardNormTimeInMonth){
        int hoursInShortDays = 0;
        while(standardNormTimeInMonth % STANDARD_TIME_IN_DAY != 0){
            ++standardNormTimeInMonth;
            ++hoursInShortDays;
        }
        double amountDay = standardNormTimeInMonth / STANDARD_TIME_IN_DAY;
        double newWorkTimeInMonth =  FLOAT_TIME_IN_DAY * amountDay - hoursInShortDays;
        setWorkTimeInMonth(newWorkTimeInMonth);
    }


    private void setFloatDay(double floatTime, int counterFloatDay, int amountUninitializedDays, final int AMOUNT_OF_DAYS){
        double frequency = calculateFrequency(counterFloatDay, amountUninitializedDays);
        double currentFrequency = 0;
        int amountWorkDay = 0;

        for(int indexDay = 0; indexDay < AMOUNT_OF_DAYS; ++indexDay){
            if(getWorkTime(indexDay) == UNINITIALIZED_VALUE){
                if(currentFrequency + 1 < frequency  && amountWorkDay < amountUninitializedDays - counterFloatDay){
                    ++currentFrequency;
                    ++amountWorkDay;
                }
                else{
                    setWorkTime(indexDay, floatTime);
                    currentFrequency = 0;
                }
            }
        }
    }


    public void generateGraph(final int AMOUNT_OF_DAYS, int amountUninitializedDays, double sumTimesUninitializedDays){
        double averageWorkTime;
        if(amountUninitializedDays != 0) averageWorkTime = sumTimesUninitializedDays / amountUninitializedDays;
        else averageWorkTime = sumTimesUninitializedDays;

        int minWorkTime = (int) averageWorkTime;
        int maxWorkTime = minWorkTime > averageWorkTime ? minWorkTime - 1 : minWorkTime + 1;
        double floatTime = minWorkTime < maxWorkTime ? minWorkTime + MINUTE_FOR_FLOAT_TIME : maxWorkTime + MINUTE_FOR_FLOAT_TIME;

        int counterFloatDay = 0;
        double sumFloatDay = 0;
        while((sumTimesUninitializedDays - sumFloatDay + ACCEPTABLE_ACCURACY) % 1 > NEGLIGIBLE_TIME_INTERVAL){
            sumFloatDay += floatTime;
            ++counterFloatDay;
        }
        if(counterFloatDay != 0) setFloatDay(floatTime, counterFloatDay, amountUninitializedDays, AMOUNT_OF_DAYS);

        amountUninitializedDays -= counterFloatDay;
        sumTimesUninitializedDays -= sumFloatDay;
        super.generateGraph(AMOUNT_OF_DAYS, amountUninitializedDays, sumTimesUninitializedDays);
    }
}
