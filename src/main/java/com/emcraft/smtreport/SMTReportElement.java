package com.emcraft.smtreport;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class SMTReportElement {

	private String layoutName;
	private String station;
	private int layoutsAssembled;
	private int pcbsAssembled;
	private int layoutLoadTime;
	private int assemblyTime;
	private int glueTime;
	private List<SMTReportElementPart> partsConsumed = new ArrayList<SMTReportElementPart>();

	public void addComponentData(SMTReportElementPart componentData) {
		partsConsumed.add(componentData);
	}

	public void print() {
		System.out.println("Board: " + layoutName + " Station: " + station
				+ " Qty: " + pcbsAssembled);
		for (SMTReportElementPart part : partsConsumed) {
			part.print();
		}
	}

	public class SMTReportElementPart {
		private String component;
		private int qtyPlaced;
		private int time;
		private int mechanicalFailures;
		private int electricalFailures;
		private int placementFailures;
		private int otherFailures;
		private int qtyConsumed;

		public String getComponent() {
			return component;
		}

		public void print() {
			System.out.println("\t" + component + "\t" + qtyPlaced + "\t"
					+ qtyConsumed);

		}

		public void setComponent(String component) {
			this.component = component;
		}

		public int getQtyPlaced() {
			return qtyPlaced;
		}

		public void setQtyPlaced(int qtyPlaced) {
			this.qtyPlaced = qtyPlaced;
		}

		public int getMechanicalFailures() {
			return mechanicalFailures;
		}

		public void setMechanicalFailures(int mechanicalFailures) {
			this.mechanicalFailures = mechanicalFailures;
		}

		public int getElectricalFailures() {
			return electricalFailures;
		}

		public void setElectricalFailures(int electricalFailures) {
			this.electricalFailures = electricalFailures;
		}

		public int getPlacementFailures() {
			return placementFailures;
		}

		public void setPlacementFailures(int placementFailures) {
			this.placementFailures = placementFailures;
		}

		public int getOtherFailures() {
			return otherFailures;
		}

		public void setOtherFailures(int otherFailures) {
			this.otherFailures = otherFailures;
		}

		public int getQtyConsumed() {
			return qtyConsumed;
		}

		public void setQtyConsumed(int qtyConsumed) {
			this.qtyConsumed = qtyConsumed;
		}
	}

	public String getLayoutName() {
		return layoutName;
	}

	public void setLayoutName(String layoutName) {
		this.layoutName = layoutName;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public int getPcbsAssembled() {
		return pcbsAssembled;
	}

	public void setPcbsAssembled(int pcbsAssembled) {
		this.pcbsAssembled = pcbsAssembled;
	}

	public List<SMTReportElementPart> getPartsConsumed() {
		return partsConsumed;
	}
}
