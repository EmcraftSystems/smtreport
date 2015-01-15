package com.emcraft.smtreport;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import com.emcraft.smtreport.SMTReportElement.SMTReportElementPart;

public class SMTReportProcessor {

	public static int BOARD_NAME = 1;
	public static int STATION_NAME = 2;
	public static int PCB_ASSEMBLED = 4;
	public static int COMPONENT = 8;
	public static int NUMBER_PLACED = 9;
	public static int MECHNICAL_FAILURE = 11;
	public static int ELECTRICAL_FAILURE = 12;
	public static int PICKING_FAILURE = 13;
	public static int PLACEMENT_FAILURE = 14;
	public static int OTHER_FAILURE = 15;
	public static int CONSUMED = 16;
	public static String BOARD_REPORT = "-b";
	public static String COMPONENTS_REPORT = "-u";

	public static void main(String[] args) {
		// Uncomment this for testing
		// if (args == null || args.length == 0) {
		// args = new String[] { COMPONENTS_REPORT, "file_1.txt", "file_2.txt"
		// };
		// }
		if (args != null && args.length == 3) {
			File f1 = new File(args[1]);
			File f2 = new File(args[2]);
			if (f1.exists() && f2.exists()) {
				try {
					new SMTReportProcessor().generateReport(args[0], args[1],
							args[2]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Invalid FilePath");
			}
		} else {
			System.out.println("Parameter Missing");
		}
	}

	public void generateReport(String type, String file1, String file2)
			throws Exception {

		SMTReport report1 = readFile(file1, CsvPreference.TAB_PREFERENCE);
		SMTReport report2 = readFile(file2, CsvPreference.TAB_PREFERENCE);
		report1.print();

		if (BOARD_REPORT.equals(type)) {
			generateBoradsReport(report1, report2);
		} else if (COMPONENTS_REPORT.equals(type)) {
			generateComponentsReport(report1, report2);
		} else {
			System.out.println("\n" + " Invalid Command ");
		}
	}

	private SMTReport readFile(String fileSource, CsvPreference csvPreference)
			throws Exception {
		ICsvListReader listReader = null;
		List<Object> reportList = null;

		SMTReport result = new SMTReport();

		try {
			listReader = new CsvListReader(new FileReader(fileSource),
					csvPreference);
			listReader.getHeader(true);

			SMTReportElement currentReportElement = new SMTReportElement();

			while (listReader.read() != null) {

				int numColumns = listReader.length();

				reportList = listReader
						.executeProcessors(getProcessors(numColumns));

				String layoutName = (String) reportList.get(BOARD_NAME);

				if (layoutName != null) {
					/*
					 * This means we are at the beginning of a new board/station
					 * section in the report file.
					 */
					currentReportElement = new SMTReportElement();
					result.addElement(currentReportElement);

					currentReportElement.setLayoutName(layoutName);
					currentReportElement.setStation((String) reportList
							.get(STATION_NAME));
					currentReportElement.setPcbsAssembled(Integer
							.valueOf((String) reportList.get(PCB_ASSEMBLED)));
				}

				if (numColumns < 16) {
					/*
					 * This means component usage data is not available.
					 */
					continue;
				}

				SMTReportElementPart componentData = currentReportElement.new SMTReportElementPart();
				componentData.setComponent((String) reportList.get(COMPONENT));
				componentData.setQtyPlaced(Integer.valueOf((String) reportList
						.get(NUMBER_PLACED)));
				componentData.setElectricalFailures((Integer) reportList
						.get(ELECTRICAL_FAILURE));
				componentData.setMechanicalFailures((Integer) reportList
						.get(MECHNICAL_FAILURE));
				componentData.setPlacementFailures((Integer) reportList
						.get(PLACEMENT_FAILURE));
				componentData.setOtherFailures((Integer) reportList
						.get(OTHER_FAILURE));
				componentData
						.setQtyConsumed((Integer) reportList.get(CONSUMED));

				currentReportElement.addComponentData(componentData);
			}
		} finally {
			if (listReader != null) {
				listReader.close();
			}
		}
		return result;
	}

	private CellProcessor[] getProcessors(int howMany) {
		List<CellProcessor> processors = new ArrayList<CellProcessor>();

		processors.add(null); // empty
		processors.add(null); // Layout name
		processors.add(null); // Station
		processors.add(null); // Layouts assembled
		processors.add(null); // PCBs assembled
		processors.add(null); // Layout load time[s]
		processors.add(null); // Assembly time[s]/PCB
		processors.add(null); // Glue time[s]/PCB
		processors.add(new NotNull()); // Component
		processors.add(new NotNull()); // Number placed
		processors.add(null); // Time[s]/comp.
		processors.add(new Optional(new ParseInt())); // Mechanical failures
		processors.add(new Optional(new ParseInt())); // Electrical failures
		processors.add(new Optional(new ParseInt())); // Picking failures
		processors.add(new Optional(new ParseInt())); // Placement failures
		processors.add(new Optional(new ParseInt())); // Other failures
		processors.add(new Optional(new ParseInt())); // Consumed

		return processors.subList(0, howMany).toArray(new CellProcessor[0]);
	}

	private void generateBoradsReport(SMTReport oldReport, SMTReport newReport) {

		/*
		 * TODO: add some verification that the layout name is one and the same
		 * in the both reports.
		 */
		System.out
				.println(oldReport.getLayoutName()
						+ "\t\t"
						+ (newReport.getPCBsAssembled() - oldReport
								.getPCBsAssembled()));
	}

	private void generateComponentsReport(SMTReport oldReport,
			SMTReport newReport) {

		/*
		 * TODO: add some verification that all quantities in the new report are
		 * bigger than the corresponding quantities in the old report.
		 */
		System.out.println(String.format("%20s,%15s,%15s,%15s",
				"Component Name", "Qty Placed", "Qty Attrition", "Qty Total"));
		System.out
				.println(String
						.format("-----------------------------------------------------------------------"));
		for (String partNumber : newReport.getAllParts()) {

			int qtyPlaced = newReport.getQtyPlaced(partNumber)
					- oldReport.getQtyPlaced(partNumber);
			int qtyTotal = newReport.getQtyConsumed(partNumber)
					- oldReport.getQtyConsumed(partNumber);
			int qtyAttrition = qtyTotal - qtyPlaced;
			System.out.println(String.format("%20s,%15s,%15s,%15s", partNumber,
					qtyPlaced, qtyAttrition, qtyTotal));
		}
	}
}
