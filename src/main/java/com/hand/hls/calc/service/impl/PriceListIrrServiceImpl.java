package com.hand.hls.calc.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hls.calc.dto.HlsPriceListConfigBT;
import com.hand.hls.calc.service.IPriceListIrrService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.utils.MathUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author yang.yang07@hand-china.com
 */
@Service
public class PriceListIrrServiceImpl implements IPriceListIrrService {
    public static final int MAX_EXECUTE_COUNT = 100;
    @Autowired
    private ConContractQuatationServiceImpl quotationService;
    private static final double DEFAULT_PRECISION = 0.000001D;
    private static final int DEFAULT_SCALE = 9;
    private static final double DEFAULT_HIGHER_VALUE = 100D;
    private static final String NULL = "null";
    private Logger logger = LoggerFactory.getLogger(getClass());
    private NumberFormat numberFormat;

    {
        numberFormat = NumberFormat.getNumberInstance(Locale.CHINA);
        numberFormat.setGroupingUsed(false);

    }

    private enum SeekType {
        //        更小值计算
        GO_LOWER,
        //        小值到中值
        LOWER_HALF,
        //        中值到大值
        HALF_HIGHER,
        //        更大值计算
        GO_HIGHER
    }

    private enum SeekTrend {
        NO_LOWER,
        LOWER,
        GREATER,
        NO_GREATER
    }

    @Override
    public Object goalSeek(JSONObject sheetsObject, JSONArray cellValues, HlsPriceListConfigBT targetCell, Double targetIrr) throws HlsCusException {
//        logger.debug("Goal seek, config button: {}", targetCell.toString());
//        if(CollectionUtils.isNotEmpty(cellValues)){
//            logger.debug("Goal seek, cellValues: {}", cellValues.toJSONString());
//        }
////        1. read sheets jsonObject
////        JSONObject sheetsObject = getSheets(priceList);
////        2. create poi sheet
//        XSSFWorkbook wb = new XSSFWorkbook();
//        XSSFSheet sheet = wb.createSheet();
////        3. load sheets jsonObject
//        quotationService.readSheet(wb, sheet, sheetsObject);
////        4. update cell values
//        quotationService.updateSheet(cellValues, null, sheet, targetCell.getPriceList());
//
//        final String targetColumnCode = targetCell.getTargetColumnCode();
//        final String variableColumnCode = targetCell.getVariableColumnCode();
//        final String targetColumnValue = targetCell.getTargetColumnValue();
//
//        //目标单元格
//        if(StringUtils.isEmpty(targetColumnCode) || NULL.equals(targetColumnCode)){
//            throw new HlsCusException("目标单元格参数不能为空，请检查价目表配置！");
//        }
//
//        //可变单元格
//        if(StringUtils.isEmpty(variableColumnCode) || NULL.equals(variableColumnCode)){
//            throw new HlsCusException("可变单元格参数不能为空，请检查价目表配置！");
//        }
//
//        //目标值
//        if(StringUtils.isEmpty(targetColumnValue) || NULL.equals(targetColumnValue)){
//            throw new HlsCusException("目标值取值单元格不能为空，请检查价目表配置！");
//        }
//
////        5. return target cell value
//        double lower = 0;
//        double higher = DEFAULT_HIGHER_VALUE;
//
//        final ConContractQuatationServiceImpl.CellPosition variableCellPosition = quotationService.parsePosition(variableColumnCode);
//        final ConContractQuatationServiceImpl.CellPosition targetCellPosition = quotationService.parsePosition(targetColumnCode);
//        final ConContractQuatationServiceImpl.CellPosition targetCellValuePosition = quotationService.parsePosition(targetColumnValue);
//        XSSFFormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
//        final Double guessValue = targetCell.getGuessValue();
//        Double returnGuessValue = goalSeek(sheet, evaluator, targetCellPosition, variableCellPosition, targetCellValuePosition, guessValue, targetIrr);
//        return roundReturnGuessValue(targetCell, returnGuessValue);
        return null;
    }

    /**
     * 根据前台配置的字段判断最后单变量的结果如何取整
     * @param targetCell 前台配置的参数（小数位数，取整方式）
     * @param returnGuessValue 取整前的结果
     * @return 取整后的结果
     */
    private Double roundReturnGuessValue(HlsPriceListConfigBT targetCell, Double returnGuessValue) {
//        //两个字段同时维护才做处理
//        if (targetCell.getVariableColumnScale() == null || StringUtils.isEmpty(targetCell.getVariableColumnRoundMode())) {
//            return returnGuessValue;
//        }
//        int returnScale = targetCell.getVariableColumnScale().intValue();
//        RoundingMode roundingMode;
//        switch (targetCell.getVariableColumnRoundMode()) {
//            case "ROUND_UP":
//                roundingMode = RoundingMode.UP;
//                break;
//            case "ROUND_DOWN":
//                roundingMode = RoundingMode.DOWN;
//                break;
//            case "ROUND_HALF_UP":
//                roundingMode = RoundingMode.HALF_UP;
//                break;
//            default:
//                return returnGuessValue;
//        }
//        if (returnGuessValue != null) {
//            returnGuessValue = new BigDecimal(returnGuessValue.toString()).setScale(returnScale, roundingMode).doubleValue();
//        }
//        return returnGuessValue;
        return null;
    }

    public Double goalSeek(XSSFSheet sheet,
                           XSSFFormulaEvaluator evaluator,
                           ConContractQuatationServiceImpl.CellPosition targetCellPosition,
                           ConContractQuatationServiceImpl.CellPosition variableCellPosition,
                           ConContractQuatationServiceImpl.CellPosition targetCellValuePosition,
                           Double guessValue,
                           Double targetIrr) {
        double sourceVariableValue;
        if (guessValue != null) {
            sourceVariableValue = guessValue;
        } else {
            sourceVariableValue = getCellNumericValue(getCell(sheet, variableCellPosition),variableCellPosition);
        }
        final double sourceTargetValue = fx(sheet, evaluator, targetCellPosition, variableCellPosition, sourceVariableValue);
        final double secondValue = fx(sheet, evaluator, targetCellPosition, variableCellPosition, MathUtil.mul(sourceVariableValue,0.99));
        double target;
        if(targetIrr != null){
            target = targetIrr;
        }else{
            target = getCellNumericValue(sheet, evaluator,targetCellValuePosition);
        }
        final double sourceValueDistance = absSub(sourceTargetValue, target);
        final double secondValueDistance = absSub(secondValue, target);

        SeekTrend currentValueTrend = null;
        SeekTrend lastValueTrend = null;
        double lowerLimit;
        double upperLimit;

        if (secondValueDistance > sourceValueDistance) {
            currentValueTrend = SeekTrend.GREATER;
            lowerLimit = sourceVariableValue;
            upperLimit = mul(sourceVariableValue, 2);
            lastValueTrend = SeekTrend.NO_GREATER;
        } else {
            currentValueTrend = SeekTrend.LOWER;
            lowerLimit = mul(sourceVariableValue, 0.5);
            upperLimit = sourceVariableValue;
            lastValueTrend = SeekTrend.NO_LOWER;
        }

        int seekCount = 0;

//        当前猜测值计算结果与target的差
        double currentDistance;
//        上次猜测值目标单元格与target的差
        double lastDistance = sourceValueDistance;
//        当前猜测值
        double currentGuessValue;
//        上次猜测值
        double lastGuessValue = sourceVariableValue;
//        上次猜测值目标单元格值
        double lastTargetCellValue = sourceTargetValue;

//        enable loop
        while (seekCount < MAX_EXECUTE_COUNT) {
            seekCount++;

            if (lastValueTrend == SeekTrend.NO_GREATER) {
                currentGuessValue = upperLimit;
            } else if (lastValueTrend == SeekTrend.NO_LOWER) {
                currentGuessValue = lowerLimit;
            } else {
                currentGuessValue = middle(lowerLimit, upperLimit);
            }
            final double targetCellValue = fx(sheet, evaluator, targetCellPosition, variableCellPosition, currentGuessValue);
            currentDistance = absSub(targetCellValue, target);

            if (!nearby(target, targetCellValue) && absSub(currentGuessValue, lastGuessValue) > DEFAULT_PRECISION && absSub(targetCellValue, lastTargetCellValue) > DEFAULT_PRECISION) {

                logger.debug("Goal Seek[{}]. currentGuessValue:{},targetCellValue:{}, currentDis:{}, lastDis:{}, lower: {}, upper: {}, lastValueTrend:{}",seekCount,  currentGuessValue, targetCellValue, currentDistance, lastDistance, lowerLimit, upperLimit, lastValueTrend);
                if (currentDistance <= DEFAULT_PRECISION || absSub(lowerLimit, upperLimit) <= DEFAULT_PRECISION) {
                    return currentGuessValue;
                }

                SeekTrend currentTrend = currentValueTrend;
//                currentValueTrend only shows LOWER or GREATER
                if (currentValueTrend == SeekTrend.LOWER) {
                    if (currentDistance > lastDistance || between(targetCellValue, lastTargetCellValue, target)) {
                        currentTrend = SeekTrend.GREATER;
                        lastValueTrend = SeekTrend.GREATER;
                        lowerLimit = currentGuessValue;
                        upperLimit = lastGuessValue;
                    } else {
                        if (lastValueTrend == SeekTrend.NO_LOWER) {
                            lowerLimit = mul(currentGuessValue, 0.5);
                        }
                        upperLimit = currentGuessValue;
                    }
                } else {
                    if (currentDistance > lastDistance || between(targetCellValue, lastTargetCellValue, target)) {
                        currentTrend = SeekTrend.LOWER;
                        lastValueTrend = SeekTrend.LOWER;
                        lowerLimit = lastGuessValue;
                        upperLimit = currentGuessValue;
                    } else {
                        if (lastValueTrend == SeekTrend.NO_GREATER) {
                            lowerLimit = currentGuessValue;
                            upperLimit = mul(currentGuessValue, 2);
                        } else {
                            lowerLimit = currentGuessValue;
                        }
                    }
                }
                logger.debug("currentValueTrend:{}, currentTrend:{}, lastValueTrend:{} ");
//            pre loop
//          sourceVariableCellValue: options.sourceVariableCellValue,
//          sourceVariableValue
//          sourceTargetCellValue: options.sourceTargetCellValue,
//          sourceTargetValue
//          directFlag: currentDirectFlag,
                currentValueTrend = currentTrend;
//          lastDisValue: currentDisValue,
                lastDistance = currentDistance;
//          lastVariableCellValue: options.variableCell.value(),
                lastGuessValue = currentGuessValue;
//          lastTargetCellValue: options.targetCell.value(),
                lastTargetCellValue = targetCellValue;
//          lastVariableFlag: lastVariableFlag
//                lastValueTrend = lastValueTrend;
            } else {
                logger.debug("Goal Seek[{}] empty.currentGuessValue:{},targetCellValue:{}, currentDis:{}, lastDis:{}, lower: {}, upper: {}", currentGuessValue, targetCellValue, seekCount, currentDistance, lastDistance, lowerLimit, upperLimit);
                return currentGuessValue;
            }


        }
//        end loop

        throw new RuntimeException("超出计算最大次数");

    }

    private double calc(XSSFSheet sheet,
                        XSSFFormulaEvaluator evaluator,
                        ConContractQuatationServiceImpl.CellPosition targetCellPosition,
                        ConContractQuatationServiceImpl.CellPosition variableCellPosition,
                        double lower,
                        double higher,
                        double target,
                        Double guessValue) {
        int executeCount = 1;
        double lastMiddle = -1D;
        double middleValue = -1D;
        double lastLower = -1D;
        double lowerValue = -1D;
        double lastHigher = -1D;
        double higherValue = -1D;
        SeekType seekType = null;

        if (guessValue != null) {
            final double guessValueResult = fx(sheet, evaluator, targetCellPosition, variableCellPosition, guessValue);
            if (nearby(guessValueResult, target)) {
                return guessValue;
            }
            higher = guessValue;
        }
        while (executeCount < MAX_EXECUTE_COUNT) {
            executeCount++;
            double middle = middle(lower, higher);
            if (nearby(lower, higher)) {
                return middle;
            }
            middleValue = fx(sheet, evaluator, targetCellPosition, variableCellPosition, middle);
            logger.debug("Goal seek calculating. lower:{}, lowerValue: {}, middle:{}, middleValue: {}, higher: {}, higherValue: {}, target: {}",
                    lower, lowerValue, middle, middleValue, higher, higherValue, target);
            if (nearby(middleValue, target)) {
                return middle;
            }
            if (seekType == null || seekType == SeekType.GO_LOWER) {
                lowerValue = fx(sheet, evaluator, targetCellPosition, variableCellPosition, lower);
                if (nearby(lowerValue, target)) {
                    return lower;
                }
            }
            if (seekType == null || seekType == SeekType.GO_HIGHER) {
                higherValue = fx(sheet, evaluator, targetCellPosition, variableCellPosition, higher);
                if (nearby(higherValue, target)) {
                    return higher;
                }
            }
//          go higher or go lower
            if (target < lowerValue && target < middleValue && target < higherValue) {
//              lower than any of lower,middle,higher
                higher = lower;
                lower = MathUtil.div(lower , 2);
                seekType = SeekType.GO_LOWER;
                higherValue = lowerValue;
                continue;
            }
            if (target > lowerValue && target > middleValue && target > higherValue) {
//              greater than any of lower,middle,higher
//                临时用一下middle变量保存中间值，middle赋值没有任何含义
                middle = higher;
                higher = MathUtil.mul(2 , higher);
                lower = middle;
                seekType = SeekType.GO_HIGHER;
                lowerValue = higherValue;
            } else if (target > lowerValue && target < middle) {
                higher = middle;
                seekType = SeekType.LOWER_HALF;
                higherValue = middleValue;

            } else if (target < higherValue && target > middle) {
                lower = middle;
                seekType = SeekType.HALF_HIGHER;
                lowerValue = middleValue;
            }

        }
        throw new RuntimeException("超出计算最大次数");
    }


    private double fx(XSSFSheet sheet,
                      XSSFFormulaEvaluator evaluator,
                      ConContractQuatationServiceImpl.CellPosition targetCellPosition,
                      ConContractQuatationServiceImpl.CellPosition variableCellPosition,
                      double variable) {
////        1. clear cache
//        evaluator.clearAllCachedResultValues();
////        2. set value to variable cell
//        final XSSFCell variableCell = getCell(sheet, variableCellPosition);
//        variableCell.setCellValue(variable);
////        3. get value from target cell
//        final XSSFCell cell = getCell(sheet, targetCellPosition);
//        if (cell.getCellType() == CellType.FORMULA) {
//            final CellValue evaluate = evaluator.evaluate(cell);
//            return getCellNumericValue(evaluate,targetCellPosition);
//        }
//        return getCellNumericValue(cell,targetCellPosition);
        return 0;
    }


    private double getCellNumericValue(XSSFSheet sheet,
                      XSSFFormulaEvaluator evaluator,
                      ConContractQuatationServiceImpl.CellPosition targetCellPosition,
                      ConContractQuatationServiceImpl.CellPosition variableCellPosition,
                      double variable) {
//        1. clear cache
        evaluator.clearAllCachedResultValues();
//        2. set value to variable cell
        final XSSFCell variableCell = getCell(sheet, variableCellPosition);
        variableCell.setCellValue(variable);
//        3. get value from target cell
        final XSSFCell cell = getCell(sheet, targetCellPosition);
//        if (cell.getCellType() == CellType.FORMULA) {
//            final CellValue evaluate = evaluator.evaluate(cell);
//            return getCellNumericValue(evaluate,targetCellPosition);
//        }
        return getCellNumericValue(cell,targetCellPosition);
    }

    private double getCellNumericValue(XSSFSheet sheet,
                                       XSSFFormulaEvaluator evaluator,
                                       ConContractQuatationServiceImpl.CellPosition cellPosition) {
//        1. clear cache
        evaluator.clearAllCachedResultValues();
//        2. get value from target cell
        final XSSFCell cell = getCell(sheet, cellPosition);
//        if (cell.getCellType() == CellType.FORMULA) {
//            final CellValue evaluate = evaluator.evaluate(cell);
//            return getCellNumericValue(evaluate,cellPosition);
//        }
        return getCellNumericValue(cell,cellPosition);
    }

    private double getCellNumericValue(CellValue cellValue, ConContractQuatationServiceImpl.CellPosition cellPosition) {
//        if (cellValue.getCellType() == CellType.NUMERIC) {
//            return cellValue.getNumberValue();
//        }
        throw new RuntimeException("单元格" + cellPosition.getCellPosition() + "不是数字类型");
    }

    private double getCellNumericValue(XSSFCell cellValue, ConContractQuatationServiceImpl.CellPosition cellPosition) {
//        if (cellValue.getCellType() == CellType.NUMERIC) {
//            return cellValue.getNumericCellValue();
//        }
        throw new RuntimeException("单元格" + cellPosition.getCellPosition() + "不是数字类型");
    }

    private XSSFCell getCell(XSSFSheet sheet, ConContractQuatationServiceImpl.CellPosition cellPosition) {
        XSSFRow row = sheet.getRow(cellPosition.getRowIndex());
        if (row == null) {
            row = sheet.createRow(cellPosition.getRowIndex());
        }
        XSSFCell cell = row.getCell(cellPosition.getCellIndex());
        if (cell == null) {
            cell = row.createCell(cellPosition.getCellIndex());
            cell.setCellType(CellType.NUMERIC);
        }
        return cell;
    }

    private boolean nearby(double va, double vb) {
        return Math.abs(va - vb) < DEFAULT_PRECISION;
    }

    private double mul(double a, double mul) {
        final BigDecimal bigDecimal = BigDecimal.valueOf(a);
        final BigDecimal mulDecimal = BigDecimal.valueOf(mul);
        return bigDecimal.multiply(mulDecimal).setScale(DEFAULT_SCALE, RoundingMode.HALF_UP).doubleValue();
    }

    private double middle(double lower, double higher) {
        final BigDecimal lowerDecimal = BigDecimal.valueOf(lower);
        final BigDecimal higherDecimal = BigDecimal.valueOf(higher);
        final BigDecimal sum = lowerDecimal.add(higherDecimal).setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
        final BigDecimal divide = sum.divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
        return divide.doubleValue();
    }

    private double absSub(double a, double b) {
        final BigDecimal lowerDecimal = BigDecimal.valueOf(a);
        final BigDecimal higherDecimal = BigDecimal.valueOf(b);
        return Math.abs(lowerDecimal.subtract(higherDecimal).doubleValue());
    }


    private boolean between(double currentTargetCellValue, double lastTargetCellValue, double target) {
        return (target > currentTargetCellValue && target < lastTargetCellValue) || (target < currentTargetCellValue && target > lastTargetCellValue);
    }

}
