package com.emcraft.smtreport;

import java.util.ArrayList;
import java.util.List;

import com.emcraft.smtreport.SMTReportElement.SMTReportElementPart;

public class SMTReport {
	private List<SMTReportElement> reportElements = new ArrayList<SMTReportElement>();

	public void addElement(SMTReportElement reportElement) {
		reportElements.add(reportElement);
	}

	public String getLayoutName() {
		if (reportElements.isEmpty()) {
			return "";
		}

		/*
		 * We assume that each report contains information about a single layout
		 * only. That is always the case in the files we deal with. The multiple
		 * elements are to account for multiple SMT stations.
		 */
		return reportElements.get(0).getLayoutName();
	}

	public int getPCBsAssembled() {
		int result = 0;

		for (SMTReportElement reportElement : reportElements) {
			result += reportElement.getPcbsAssembled();
		}

		return result;
	}

	public List<String> getAllParts() {
		List<String> result = new ArrayList<String>();

		for (SMTReportElement reportElement : reportElements) {
			for (SMTReportElementPart part : reportElement.getPartsConsumed()) {
				result = addPartToListIfNotAlreadyThere(result,
						part.getComponent());
			}
		}
		return result;
	}

	private List<String> addPartToListIfNotAlreadyThere(
			List<String> existingParts, String newPart) {

		for (String partInList : existingParts) {
			if (partInList.equalsIgnoreCase(newPart)) {
				return existingParts;
			}
		}
		existingParts.add(newPart);
		return existingParts;
	}

	public int getQtyPlaced(String partNumber) {
		int result = 0;

		for (SMTReportElement reportElement : reportElements) {
			for (SMTReportElementPart part : reportElement.getPartsConsumed()) {
				if (part.getComponent().equalsIgnoreCase(partNumber)) {
					result += part.getQtyPlaced();
				}
			}
		}
		return result;
	}

	public int getQtyConsumed(String partNumber) {
		int result = 0;

		for (SMTReportElement reportElement : reportElements) {
			for (SMTReportElementPart part : reportElement.getPartsConsumed()) {
				if (part.getComponent().equalsIgnoreCase(partNumber)) {
					result += part.getQtyConsumed();
				}
			}
		}
		return result;
	}

	public void print() {
		for (SMTReportElement reportElement : reportElements) {

			System.out.println("------------------------------------");
			reportElement.print();
		}
	}
}
