package com.google.engedu.ghost;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BinarySearcher {

	private List<String> list;
	
	public BinarySearcher(List<String> list) {
		this.list = list;
	}
	
	private int internalSearch(List<String> list, String obj, int offset) {
		if(list.isEmpty()) {
			return -1;
		} else {
			int middleIndex = (int) Math.floor(list.size() / 2d);
			String middle = list.get(middleIndex);

			if(middle.startsWith(obj)) {
				return middleIndex + offset;
			} else {
				int comparation = obj.compareTo(middle);

				if(comparation < 0) {
					if(middleIndex > 0) {
						return internalSearch(list.subList(0, middleIndex), obj, offset);
					} else {
						return -1;
					}
				} else {
					if(middleIndex + 1 < list.size()) {
						return internalSearch(list.subList(middleIndex + 1, list.size()), obj, offset + middleIndex + 1);
					} else {
						return -1;
					}
				}
			}
		}
	}
	
	public int search(String obj) {
		return internalSearch(new ArrayList<>(list), obj, 0);
	}

	private int getRangeStart(List<String> list, String obj, int offset) {
		if(list.isEmpty()) {
			return -1;
		} else {
			int middleIndex = (int) Math.floor(list.size() / 2d);
			String middle = list.get(middleIndex);
			String prev = middleIndex == 0 ? null : list.get(middleIndex - 1);

			if(middle.startsWith(obj) && (prev == null || !prev.startsWith(obj))) {
				return middleIndex + offset;
			} else if(prev != null && prev.startsWith(obj)) {
				return getRangeStart(list.subList(0, middleIndex), obj, offset);
			} else {
				int comparation = obj.compareTo(middle);

				if(comparation < 0) {
					if(middleIndex > 0) {
						return getRangeStart(list.subList(0, middleIndex), obj, offset);
					} else {
						return -1;
					}
				} else {
					if(middleIndex + 1 < list.size()) {
						return getRangeStart(list.subList(middleIndex + 1, list.size()), obj, offset + middleIndex + 1);
					} else {
						return -1;
					}
				}
			}
		}
	}

	private int getRangeEnd(List<String> list, String obj, int offset) {
		if(list.isEmpty()) {
			return -1;
		} else {
			int middleIndex = (int) Math.floor(list.size() / 2d);
			String middle = list.get(middleIndex);
			String next = middleIndex >= (list.size() - 1) ? null : list.get(middleIndex + 1);
			Log.d("getRangeEnd", "middle: " + middle + ", next: " + next);

			if(middle.startsWith(obj) && (next == null || !next.startsWith(obj))) {
				return middleIndex + offset;
			} else if(next != null && next.startsWith(obj)) {
				return getRangeEnd(list.subList(middleIndex + 1, list.size()), obj, offset + middleIndex + 1);
			} else {
				int comparation = obj.compareTo(middle);

				if(comparation < 0) {
					if(middleIndex > 0) {
						return getRangeEnd(list.subList(0, middleIndex), obj, offset);
					} else {
						return -1;
					}
				} else {
					if(middleIndex + 1 < list.size()) {
						return getRangeEnd(list.subList(middleIndex + 1, list.size()), obj, offset + middleIndex + 1);
					} else {
						return -1;
					}
				}
			}
		}
	}

	public Range getRange(String obj) {
		int start = getRangeStart(new ArrayList<>(list), obj, 0);
		int end = getRangeEnd(new ArrayList<>(list), obj, 0);

		return start == -1 || end == -1 ? null : new Range(start, end);
	}

	public static class Range {
		private int start;

		private int end;

		public Range(int start, int end) {
			this.start = start;
			this.end = end;
		}

		public int getStart() {
			return start;
		}

		public int getEnd() {
			return end;
		}
	}
}
