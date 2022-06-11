package lk.express;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.bean.AdvisoryNote;

@XmlRootElement
@XmlType(name = "HoldResult", namespace = "http://express.lk")
public class HoldResult {

	private List<Integer> heldItemIds;
	private List<HeldItem> heldItems;
	private List<AdvisoryNote> advisoryNotes = new ArrayList<AdvisoryNote>();

	public List<Integer> getHeldItemIds() {
		return heldItemIds;
	}

	public void setHeldItemIds(List<Integer> heldItemIds) {
		this.heldItemIds = heldItemIds;
	}

	public List<HeldItem> getHeldItems() {
		return heldItems;
	}

	public void setHeldItems(List<HeldItem> heldItems) {
		this.heldItems = heldItems;
	}

	public List<AdvisoryNote> getAdvisoryNotes() {
		return advisoryNotes;
	}

	public void setAdvisoryNotes(List<AdvisoryNote> advisoryNotes) {
		this.advisoryNotes = advisoryNotes;
	}

	public static class HeldItem {

		private String resultIndex;
		private int sectorIndex;
		private int heldItemId;

		public HeldItem() {
		}

		public HeldItem(String resultIndex, int sectorIndex, int heldItemId) {
			this.resultIndex = resultIndex;
			this.sectorIndex = sectorIndex;
			this.heldItemId = heldItemId;
		}

		public String getResultIndex() {
			return resultIndex;
		}

		public void setResultIndex(String resultIndex) {
			this.resultIndex = resultIndex;
		}

		public int getSectorIndex() {
			return sectorIndex;
		}

		public void setSectorIndex(int sectorIndex) {
			this.sectorIndex = sectorIndex;
		}

		public int getHeldItemId() {
			return heldItemId;
		}

		public void setHeldItemId(int heldItemId) {
			this.heldItemId = heldItemId;
		}

	}
}
