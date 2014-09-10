package com.smtreport;

import java.io.FileReader;
import java.util.*;

import org.supercsv.cellprocessor.*;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.*;
import org.supercsv.prefs.CsvPreference;

public class SMTReportProcessor {

	public static int BOARD_NAME = 1;
	public static int PCB_ASSEMBLED = 4;
	public static int COMPONENT = 8;
	public static int NUMBER_PLACED = 9;
	public static int MECHNICAL_FAILURE = 11;
	public static int ELECTRICAL_FAILURE = 12;
	public static int PICKING_FAILURE = 13;
	public static int PLACEMENT_FAILURE = 14;
	public static int OTHER_FAILURE = 15;
	public static int CONSUMED = 16;
	public static String BOARD_REPORT= "-b";
	public static String COMPONENTS_REPORT= "-u";
	
	private CellProcessor[] getProcessors() {
		final CellProcessor[] processors = new CellProcessor[] { null, // empty
				null, // Layout name
				null, // Station
				null, // Layouts assembled
				null, // PCBs assembled
				null, // Layout load time[s]
				null, // Assembly time[s]/PCB
				null, // Glue time[s]/PCB
				new NotNull(), // Component
				new NotNull(), // Number placed
				new Optional(new ParseBigDecimal()), // Time[s]/comp.
				new Optional(new ParseInt()), // Mechanical failures
				new Optional(new ParseInt()),// Electrical failures
				new Optional(new ParseInt()), // Picking failures
				new Optional(new ParseInt()), // Placement failures
				new Optional(new ParseInt()), // Other failures
				new Optional(new ParseInt()) // Consumed
		};
		return processors;
	}

	protected Map<String, List<Object>> readFile(String fileSource, CsvPreference csvPreference) throws Exception {
		ICsvListReader listReader = null;
		List<Object> reportList = null;
		
		Map<String, List<Object>> map = new HashMap<String, List<Object>>();
		try {
			listReader = new CsvListReader(new FileReader(fileSource), csvPreference);
			listReader.getHeader(true);
			final CellProcessor[] processors = getProcessors();
			String nextBoardName = "";
			String prevkey = "";
			List<Object> rows = new ArrayList<Object>();
			while ((reportList = listReader.read(processors)) != null) {
				String layoutName = (String) reportList.get(BOARD_NAME);
				nextBoardName = (layoutName == null ? "" : layoutName);
				if (!("").equals(nextBoardName) && !("").equals(prevkey)) {
					map.put(prevkey, rows);
					rows = new ArrayList<Object>();
					rows.add(reportList);
				} else {
					rows.add(reportList);
				}
				if (layoutName != null)
					prevkey = layoutName;
			}
			map.put(prevkey, rows); // last Group Entry
		} finally {
			if (listReader != null) {
				listReader.close();
			}
		}
		return map;
	}

	public void generateReport(String type, String file1, String file2) throws Exception {
		Map<String, List<Object>> file1Map = readFile(file1, CsvPreference.TAB_PREFERENCE);
		Map<String, List<Object>> file2Map = readFile(file2, CsvPreference.TAB_PREFERENCE);
		if (BOARD_REPORT.equals(type)) {
			generateBoradsReport(file1Map, file2Map);
		} else if (COMPONENTS_REPORT.equals(type)) {
			generateComponentsReport(file1Map, file2Map);
		} else {
			System.out.println("\n" + " Invalid Command ");
		}
	}

	private void generateComponentsReport(Map<String, List<Object>> file1, Map<String, List<Object>> file2) {
		
		for (String key : file2.keySet()) {

			List<Object> filelist1 = file1.get(key);
			List<Object> filelist2 = file2.get(key);

			System.out.println(String.format("%20s,%20s,%20s,%20s", "Component Name", "Qty Placed", "Qty Attrition","Qty Total"));
			System.out.println(String.format("-----------------------------------------------------------------------------------------"));

			if (filelist1 == null && filelist2!=null) {
				for (int i = 0; i < filelist2.size(); i++) {
					List<?> row = (List<?>) filelist2.get(i);
					String qty = (String) row.get(NUMBER_PLACED);
					int qtyPlaced = Integer.valueOf(qty);
					int qtyTotal = (Integer) row.get(CONSUMED);
					int qtyAttrition = (Integer) row.get(MECHNICAL_FAILURE) + (Integer) row.get(ELECTRICAL_FAILURE)
							+ (Integer) row.get(PICKING_FAILURE) + (Integer) row.get(PLACEMENT_FAILURE)
							+ (Integer) row.get(OTHER_FAILURE);
					System.out.println(String.format("%20s,%20s,%20s,%20s", row.get(COMPONENT), Math.abs(qtyPlaced),
							Math.abs(qtyAttrition), Math.abs(qtyTotal)));
				}
			}
			else {
				for (int i = 0; i < filelist2.size(); i++) {
					List<?> row1 = (List<?>) filelist1.get(i);
					List<?> row2 = (List<?>) filelist2.get(i);
					String qty1 = (String) row1.get(NUMBER_PLACED);
					String qty2 = (String) row2.get(NUMBER_PLACED);
					int qtyPlaced = Integer.valueOf(qty1) - Integer.valueOf(qty2);
					int qtyTotal1 = (Integer) row1.get(CONSUMED);
					int qtyTotal2 = (Integer) row2.get(CONSUMED);
					int qtyTotal = qtyTotal1 - qtyTotal2;
					int failuefile1 = (Integer) row1.get(MECHNICAL_FAILURE) + (Integer) row1.get(ELECTRICAL_FAILURE)
							+ (Integer) row1.get(PICKING_FAILURE) + (Integer) row1.get(PLACEMENT_FAILURE)
							+ (Integer) row1.get(OTHER_FAILURE);
					int failuefile2 = (Integer) row2.get(MECHNICAL_FAILURE) + (Integer) row2.get(ELECTRICAL_FAILURE)
							+ (Integer) row2.get(PICKING_FAILURE) + (Integer) row2.get(PLACEMENT_FAILURE)
							+ (Integer) row2.get(OTHER_FAILURE);
					int qtyAttrition = failuefile1 - failuefile2;
					System.out.println(String.format("%20s,%20s,%20s,%20s", row1.get(COMPONENT), Math.abs(qtyPlaced),
							Math.abs(qtyAttrition), Math.abs(qtyTotal)));
				}
			}
		}
		
	}
	private void generateBoradsReport(Map<String, List<Object>> file1, Map<String, List<Object>> file2) {
		System.out.println("BoardName" + "\t\t\t\t" + "Qty");
		for (String key : file1.keySet()) {
			List<Object> filelist1 = file1.get(key);
			List<Object> filelist2 = file2.get(key);
			if (filelist1 == null && filelist2 != null) {
					List<?> row1 = (List<?>) filelist2.get(0);
					String qty1 = (String) row1.get(PCB_ASSEMBLED);// PCBs assembled
					int totalQty = Integer.valueOf(qty1);
					System.out.println("-----------------------------------------------");
					System.out.println(row1.get(BOARD_NAME) + "\t\t" + Math.abs(totalQty));

			} else if (filelist1 != null && filelist2 != null) {
				List<?> row1 = (List<?>) filelist1.get(0);
				List<?> row2 = (List<?>) filelist2.get(0);
				String qty1 = (String) row1.get(PCB_ASSEMBLED);// PCBs assembled
				String qty2 = (String) row2.get(PCB_ASSEMBLED);// PCBs assembled
				int totalQty = Integer.valueOf(qty1) - Integer.valueOf(qty2);
				System.out.println("-----------------------------------------------");
				System.out.println(row1.get(BOARD_NAME) + "\t\t" + Math.abs(totalQty));
			}

		}
	
	}
	public static void main(String[] args) {
		try {
			new SMTReportProcessor().generateReport(args[0], args[1], args[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
