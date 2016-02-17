package ru.polynkina.irina.graphs;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

public class FileInteraction {

    final static int INDEX_OF_SHEET = 0;

    // constants in the file graphs.xls
    final static int COL_INDEX_OF_ID = 0;
    final static int COL_INDEX_OF_NAME = 1;
    final static int COL_INDEX_OF_RULE = 2;
    final static int COL_INDEX_OF_TYPE = 3;
    final static int COL_INDEX_OF_DAYTIME = 4;
    final static int COL_INDEX_OF_DAYTIME_SIGN = 5;
    final static int COL_INDEX_OF_NIGHTTIME = 6;
    final static int COL_INDEX_OF_NIGHTTIME_SIGN = 7;

    final static String STRING_DESIGNATION_OF_DAY_GRAPHS = "DAY";
    final static String STRING_DESIGNATION_OF_SHORT_DAY_GRAPHS = "SHORT";
    final static String STRING_DESIGNATION_OF_STANDARD_GRAPHS = "STANDARD";
    final static String STRING_DESIGNATION_OF_UNIQUE_GRAPHS = "UNIQUE";
    final static String STRING_DESIGNATION_OF_FLOAT_GRAPHS = "FLOAT";
    final static String STRING_DESIGNATION_OF_DIURNAL_GRAPHS = "DIURNAL";
    final static String STRING_DESIGNATION_OF_MIX_GRAPHS = "MIX";

    // constants in the file counter_...xls
    final static int COL_INDEX_COUNTER_ID = 0;
    final static int COL_INDEX_COUNTER_VALUE = 1;

    // constants in the file templateWorkingTime.xls and in the file templateForSapHR.xls
    final static int DELTA_ROW_IN_TEMPLATE = 1;
    final static int DELTA_COL_IN_TEMPLATE = 2;
    final static int COL_INDEX_NAME_GRAPH_IN_TEMPLATE = 0;
    final static int COL_INDEX_WORK_TIME_IN_TEMPLATE = 1;
    final static int SIZE_STEP = 5;

    final static String SIGN_SHORT_DAY = "A";
    final static String SIGN_DAY_OFF = "F";
    final static int SIGN_HOLIDAY = 1;

    final static int CELL_OFFSET_FOR_TEXT = 2;
    final static int CELL_OFFSET_FOR_NUMBER = 3;

    // constants in the file dayHours.xls and in the file nightHours.xls
    final static int COL_INDEX_HOUR = 0;
    final static int COL_INDEX_HOUR_NAME = 1;


    /*******************************************************************************************************************************************
                                                        private methods
     ******************************************************************************************************************************************/


    private static String findHourName(Map<Double, String> hours, double desiredValue){
        String hourName = hours.get(desiredValue);
        if(hourName == null){
            System.out.println("Не могу найти график на: " + desiredValue + " час");
            hourName = "FREE";
        }
        return hourName;
    }


    /*******************************************************************************************************************************************
                                                        public methods
     ******************************************************************************************************************************************/


    public static void fabricateGraphs(List<Graph> graphs, final double NORM_TIME){
        try{

            FileInputStream fis = new FileInputStream("./lib/graphs.xls");
            Workbook wb = new HSSFWorkbook(fis);

            int indexRow = 0;
            while(true){
                try{

                    int id = (int) wb.getSheetAt(INDEX_OF_SHEET).getRow(indexRow).getCell(COL_INDEX_OF_ID).getNumericCellValue();
                    String name = wb.getSheetAt(INDEX_OF_SHEET).getRow(indexRow).getCell(COL_INDEX_OF_NAME).getStringCellValue();
                    String rule = wb.getSheetAt(INDEX_OF_SHEET).getRow(indexRow).getCell(COL_INDEX_OF_RULE).getStringCellValue();
                    String type = wb.getSheetAt(INDEX_OF_SHEET).getRow(indexRow).getCell(COL_INDEX_OF_TYPE).getStringCellValue();
                    double daytime = wb.getSheetAt(INDEX_OF_SHEET).getRow(indexRow).getCell(COL_INDEX_OF_DAYTIME).getNumericCellValue();
                    String daytimeSign = wb.getSheetAt(INDEX_OF_SHEET).getRow(indexRow).getCell(COL_INDEX_OF_DAYTIME_SIGN).getStringCellValue();

                    if(type.equals(STRING_DESIGNATION_OF_DAY_GRAPHS)){
                        graphs.add(new Graph(id, name, rule, daytime, daytimeSign, NORM_TIME));
                    }
                    else if(type.equals(STRING_DESIGNATION_OF_SHORT_DAY_GRAPHS)){
                        graphs.add(new GraphShort(id, name, rule, daytime, daytimeSign, NORM_TIME));
                    }
                    else if(type.equals(STRING_DESIGNATION_OF_STANDARD_GRAPHS)){
                        graphs.add(new GraphStandard(id, name, rule, daytime, daytimeSign, NORM_TIME));
                    }
                    else if(type.equals(STRING_DESIGNATION_OF_UNIQUE_GRAPHS)){
                        double uniqueTime = wb.getSheetAt(INDEX_OF_SHEET).getRow(indexRow).getCell(COL_INDEX_OF_NIGHTTIME).getNumericCellValue();
                        String uniqueTimeSign = wb.getSheetAt(INDEX_OF_SHEET).getRow(indexRow).getCell(COL_INDEX_OF_NIGHTTIME_SIGN).getStringCellValue();
                        graphs.add(new GraphUnique(id, name, rule, daytime, daytimeSign, uniqueTime, uniqueTimeSign, NORM_TIME));
                    }
                    else if(type.equals(STRING_DESIGNATION_OF_FLOAT_GRAPHS)){
                        graphs.add(new GraphFloat(id, name, rule, daytime, daytimeSign, NORM_TIME));
                    }
                    else if(type.equals(STRING_DESIGNATION_OF_DIURNAL_GRAPHS)){
                        graphs.add(new GraphDiurnal(id, name, rule, daytime, daytimeSign, NORM_TIME));
                    }
                    else if(type.equals(STRING_DESIGNATION_OF_MIX_GRAPHS)){
                        double nightTime = wb.getSheetAt(INDEX_OF_SHEET).getRow(indexRow).getCell(COL_INDEX_OF_NIGHTTIME).getNumericCellValue();
                        String nightTimeSign = wb.getSheetAt(INDEX_OF_SHEET).getRow(indexRow).getCell(COL_INDEX_OF_NIGHTTIME_SIGN).getStringCellValue();
                        graphs.add(new GraphMix(id, name, rule, daytime, daytimeSign, nightTime, nightTimeSign, NORM_TIME));
                    }
                    else{
                        System.out.println("Тип графика: " + type + " неизвестен!");
                        graphs.add(new Graph(id, name, rule, daytime, daytimeSign, NORM_TIME));
                    }

                    ++indexRow;

                }catch(NullPointerException nullExc){
                    break;
                }
            }

            wb.close();
            fis.close();

        }catch(Exception exc){
            exc.printStackTrace();
            System.exit(0);
        }
    }



    public static void readCountersForGraphs(List<Graph> graphs, final int INDEX_MONTH, final int INDEX_YEAR){
        try {
            String filename = "counter_" + INDEX_MONTH + "_" + INDEX_YEAR + ".xls";
            FileInputStream fis = new FileInputStream("./count/" + filename);
            Workbook wb = new HSSFWorkbook(fis);

            for (Graph obj : graphs) {
                int idGraph = obj.getId();
                int idCounter = (int) wb.getSheetAt(INDEX_OF_SHEET).getRow(idGraph).getCell(COL_INDEX_COUNTER_ID).getNumericCellValue();
                int valueCounter = (int) wb.getSheetAt(INDEX_OF_SHEET).getRow(idGraph).getCell(COL_INDEX_COUNTER_VALUE).getNumericCellValue();

                if (idGraph != idCounter) throw new Exception("Файл " + filename + " поврежден");
                obj.setCounter(valueCounter);
            }

            wb.close();
            fis.close();
        }catch(FileNotFoundException excFile){
            System.out.println("Не сгенерирован график за предыдущий месяц!");
            System.exit(0);
        }catch(Exception exc){
            exc.printStackTrace();
            System.exit(0);
        }
    }



    public static void writeWorkTimeInFile(final List<Graph> graphs, final int AMOUNT_OF_DAYS){
        try{

            FileInputStream fis = new FileInputStream("./lib/templateWorkingTime.xls");
            Workbook wb = new HSSFWorkbook(fis);

            CellStyle styleForDaytime = wb.createCellStyle();
            styleForDaytime.setFillPattern(CellStyle.SOLID_FOREGROUND);
            styleForDaytime.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());

            CellStyle styleForNighttime = wb.createCellStyle();
            styleForNighttime.setFillPattern(CellStyle.SOLID_FOREGROUND);
            styleForNighttime.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());

            for(Graph obj : graphs){
                Row row = wb.getSheetAt(INDEX_OF_SHEET).createRow(obj.getId() + DELTA_ROW_IN_TEMPLATE);
                Cell cell = row.createCell(COL_INDEX_NAME_GRAPH_IN_TEMPLATE);
                cell.setCellValue(obj.getName());
                cell = row.createCell(COL_INDEX_WORK_TIME_IN_TEMPLATE);
                cell.setCellValue(obj.getSumWorkTime(AMOUNT_OF_DAYS));

                int lengthRule = obj.getLengthRule();
                int currentCounter = obj.getCounter();
                for(int indexDay = 0; indexDay < AMOUNT_OF_DAYS; ++indexDay){
                    if(obj.getWorkTime(indexDay) != 0){
                        cell = row.createCell(indexDay + DELTA_COL_IN_TEMPLATE);
                        cell.setCellValue(obj.getWorkTime(indexDay));
                        if(obj.getRuleOfDay(currentCounter) == Graph.CHAR_DESIGNATION_NIGHT){
                            cell.setCellStyle(styleForNighttime);
                        }
                        else{
                            cell.setCellStyle(styleForDaytime);
                        }
                    }
                    if(++currentCounter == lengthRule) currentCounter = 0;
                }
            }

            FileOutputStream fos = new FileOutputStream("./workTime.xls");
            wb.write(fos);

            fos.close();
            wb.close();
            fis.close();

        }catch(Exception exc){
            exc.printStackTrace();
            System.exit(0);
        }
    }



    public static void readDayHours(Map<Double, String> nameDayHours){
        try{

            FileInputStream fis = new FileInputStream("./lib/dayHours.xls");
            Workbook wb = new HSSFWorkbook(fis);

            int indexRow = 0;
            while(true){
                try{

                    double hour = wb.getSheetAt(INDEX_OF_SHEET).getRow(indexRow).getCell(COL_INDEX_HOUR).getNumericCellValue();
                    String hourName = wb.getSheetAt(INDEX_OF_SHEET).getRow(indexRow).getCell(COL_INDEX_HOUR_NAME).getStringCellValue();
                    nameDayHours.put(hour, hourName);
                    ++indexRow;

                }catch(NullPointerException nullExc){
                    break;
                }
            }

            wb.close();
            fis.close();

        }catch(Exception exc){
            exc.printStackTrace();
            System.exit(0);
        }
    }



    public static void readNightHours(Map<Double, String> nameNightHours){
        try{

            FileInputStream fis = new FileInputStream("./lib/nightHours.xls");
            Workbook wb = new HSSFWorkbook(fis);

            int indexRow = 0;
            while(true){
                try{

                    double hour = wb.getSheetAt(INDEX_OF_SHEET).getRow(indexRow).getCell(COL_INDEX_HOUR).getNumericCellValue();
                    String hourName = wb.getSheetAt(INDEX_OF_SHEET).getRow(indexRow).getCell(COL_INDEX_HOUR_NAME).getStringCellValue();
                    nameNightHours.put(hour, hourName);
                    ++indexRow;

                }catch(NullPointerException nullExc){
                    break;
                }
            }

            wb.close();
            fis.close();

        }catch(Exception exc){
            exc.printStackTrace();
            System.exit(0);
        }
    }



    public static void writeGraphsIntoTemplate(final List<Graph> graphs, final Map<Double, String> nameDayHours, final Map<Double, String> nameNightHours,
                                               final Map<Integer, Integer> shortDayAndHoliday, final int AMOUNT_OF_DAYS){
        try{

            FileInputStream fis = new FileInputStream("./lib/templateForSapHR.xls");
            Workbook wb = new HSSFWorkbook(fis);

            CellStyle styleForDaytime = wb.createCellStyle();
            styleForDaytime.setFillPattern(CellStyle.SOLID_FOREGROUND);
            styleForDaytime.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());

            CellStyle styleForNighttime = wb.createCellStyle();
            styleForNighttime.setFillPattern(CellStyle.SOLID_FOREGROUND);
            styleForNighttime.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());

            for(Graph obj : graphs){
                Row row = wb.getSheetAt(INDEX_OF_SHEET).createRow(obj.getId() + DELTA_ROW_IN_TEMPLATE);
                Cell cell = row.createCell(COL_INDEX_NAME_GRAPH_IN_TEMPLATE);
                cell.setCellValue(obj.getName());
                cell = row.createCell(COL_INDEX_WORK_TIME_IN_TEMPLATE);
                cell.setCellValue(obj.getSumWorkTime(AMOUNT_OF_DAYS));

                int lengthRule = obj.getLengthRule();
                int currentCounter = obj.getCounter();
                for(int indexDay = 0; indexDay < AMOUNT_OF_DAYS; ++indexDay){
                    double hour = obj.getWorkTime(indexDay);
                    Integer codeDay = shortDayAndHoliday.get(indexDay + 1);
                    if(codeDay != null && codeDay == Graph.CODE_SHORT_DAY){
                        if(obj.getWorkTime(indexDay) != 0) ++hour;
                    }

                    String hourName;
                    if(obj.getRuleOfDay(currentCounter) == Graph.CHAR_DESIGNATION_DAY){
                        if(hour == obj.getDaytime()) hourName = obj.getDaytimeSign();
                        else hourName = findHourName(nameDayHours, hour);
                    }
                    else if(obj.getRuleOfDay(currentCounter) == Graph.CHAR_DESIGNATION_NIGHT){
                        if(hour == obj.getNightTime()) hourName = obj.getNightTimeSign();
                        else hourName = findHourName(nameNightHours, hour);
                    }
                    else{
                        if(hour == obj.getUniqueTime()) hourName = obj.getUniqueTimeSign();
                        else hourName = findHourName(nameDayHours, hour);
                    }

                    cell = row.createCell(DELTA_COL_IN_TEMPLATE + indexDay * SIZE_STEP);
                    cell.setCellValue(hourName);

                    if(obj.getRuleOfDay(currentCounter) == Graph.CHAR_DESIGNATION_WEEKEND && obj.getWorkTime(indexDay) == 0); // without instruction!
                    else if(obj.getRuleOfDay(currentCounter) == Graph.CHAR_DESIGNATION_NIGHT) cell.setCellStyle(styleForNighttime);
                    else cell.setCellStyle(styleForDaytime);

                    // set sign short day and holiday
                    if(codeDay != null){
                        switch(codeDay){
                            case Graph.CODE_SHORT_DAY:{
                                if(obj.getRuleOfDay(currentCounter) != Graph.CHAR_DESIGNATION_WEEKEND){
                                    cell = row.createCell(DELTA_COL_IN_TEMPLATE + (indexDay + 1) * SIZE_STEP - CELL_OFFSET_FOR_TEXT);
                                    cell.setCellValue(SIGN_SHORT_DAY);
                                }
                            } break;
                            case Graph.CODE_HOLIDAY:{
                                cell = row.createCell(DELTA_COL_IN_TEMPLATE + (indexDay + 1) * SIZE_STEP - CELL_OFFSET_FOR_NUMBER);
                                cell.setCellValue(SIGN_HOLIDAY);
                            } // without break!
                            case Graph.CODE_DAY_OFF:{
                                boolean isStandardOrUniqueGraph = (obj instanceof GraphStandard) || (obj instanceof GraphUnique);
                                if(isStandardOrUniqueGraph && obj.getRuleOfDay(currentCounter) != Graph.CHAR_DESIGNATION_WEEKEND){
                                    cell = row.createCell(DELTA_COL_IN_TEMPLATE + indexDay * SIZE_STEP);
                                    if(obj.getRuleOfDay(currentCounter) != Graph.CHAR_DESIGNATION_UNIVERSAL_DAY) cell.setCellValue(obj.getDaytimeSign());
                                    else cell.setCellValue(obj.getUniqueTimeSign());
                                    cell = row.createCell(DELTA_COL_IN_TEMPLATE + (indexDay + 1) * SIZE_STEP - CELL_OFFSET_FOR_TEXT);
                                    cell.setCellValue(SIGN_DAY_OFF);
                                }
                            }
                        }
                    }
                    if(++currentCounter == lengthRule) currentCounter = 0;
                }
            }

            FileOutputStream fos = new FileOutputStream("./fileForSapHR.xls");
            wb.write(fos);

            fos.close();
            wb.close();
            fis.close();

        }catch(Exception exc){
            exc.printStackTrace();
            System.exit(0);
        }
    }



    public static void writeNextCounter(final List<Graph> graphs, final int AMOUNT_OF_DAYS, final int INDEX_MONTH, final int INDEX_YEAR){
        String filename = "counter_" + INDEX_MONTH + "_" + INDEX_YEAR + ".xls";
        try{
            FileInputStream fis = new FileInputStream("./count/" + filename);
            Workbook wb = new HSSFWorkbook(fis);

            for(Graph obj : graphs){
                Row row = wb.getSheetAt(INDEX_OF_SHEET).getRow(obj.getId());
                Cell cell = row.createCell(COL_INDEX_COUNTER_ID);
                cell.setCellValue(obj.getId());

                int nextCounter = AMOUNT_OF_DAYS % obj.getLengthRule();
                nextCounter += obj.getCounter();
                nextCounter %= obj.getLengthRule();
                cell = row.createCell(COL_INDEX_COUNTER_VALUE);
                cell.setCellValue(nextCounter);
            }

            int nextMonth = INDEX_MONTH + 1 > 12 ? 1 : INDEX_MONTH + 1;
            int nextYear = INDEX_MONTH + 1 > 12 ? INDEX_YEAR + 1 : INDEX_YEAR;
            String nextFilename = "counter_" + nextMonth + "_" + nextYear + ".xls";
            FileOutputStream fos = new FileOutputStream("./count/" + nextFilename);
            wb.write(fos);

            fos.close();
            wb.close();
            fis.close();
        }catch (Exception exc){
            exc.printStackTrace();
            System.exit(0);
        }
    }



    public static void deleteOldCounter(final int INDEX_MONTH, final int INDEX_YEAR){
        try{
            String filenameOldCounter = "counter_" + INDEX_MONTH + "_" + (INDEX_YEAR - 1) + ".xls";
            File oldFile = new File("./count/" + filenameOldCounter);
            oldFile.delete();
        }catch (Exception exc){
            exc.printStackTrace();
            System.exit(0);
        }
    }
}