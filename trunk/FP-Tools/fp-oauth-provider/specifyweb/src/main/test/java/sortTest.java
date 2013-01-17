import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;


public class sortTest {

	@Test
	public void test1() {
		List<String> fields = new ArrayList<String>();

		fields.add("definitionItem.name");
		fields.add("definitionItem.rankId");
		fields.add("fullName");
		fields.add("g.other");
		fields.add("a.other.foo");
		fields.add("definitionItem.c.id");
		
		Collections.sort(fields, new HqlPathComparator());
		for (String field : fields) {
			System.out.println(field);
		}
	}

	@Test
	public void test2() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("definitionItem.name", "genus");
		map.put("definitionItem.rankId", "180");
		map.put("fullName", "Crataegus");
		map.put("g.other", "other g");
		map.put("a.other.foo", "a other foo");
		map.put("definitionItem.c.id", "1");
		
		Map<String, Object> tree = toTree(map);
		
		System.out.println();
		printTree(tree);
	}

	@Test
	public void test3() {
		Foo f = new Foo();
		Object g = f;
		bar(g);
	}
	
	private void bar(Object o) {
		System.out.println("called bar(Object)");
	}
	private void bar(Foo f) {
		bar((Object) f);
		System.out.println("called bar(Foo)");
	}
	private class Foo {
		
	}
	private void printTree(Map<String, Object> tree) {
		for (String field : tree.keySet()) {
			Object value = tree.get(field);
			
			System.out.print(field + ": ");
			if (value instanceof Map) {
				System.out.print("{");
				printTree((Map<String, Object>) value);
				System.out.println("}");
			}
			else {
				System.out.println(value);
			}
		}
		
	}
	private Map<String, Object> toTree(Map<String, Object> map) {

		Map<String, Object> tree = new HashMap<String, Object>();
		
		for (String field : map.keySet()) {
			Object fieldValue = map.get(field);

			if (! field.contains(".")) {
				tree.put(field, fieldValue);
				continue;
			}
			
			String[] associations = field.split("\\.");
			
			Map<String, Object> node = tree;

			for (int i = 0; i < associations.length - 1; i++) {
				String association = associations[i];
				Map<String, Object> subtree = (Map<String, Object>) node.get(association);
				if (subtree == null) {
					subtree = new HashMap<String, Object>();
					node.put(association, subtree);
				}
				node = subtree;
			}
			node.put(associations[associations.length - 1], fieldValue);
		}

		return tree;
	}

	private class HqlPathComparator implements Comparator<String> {

		@Override
		public int compare(String o1, String o2) {
			String[] o1parts = o1.split("\\.");
			String[] o2parts = o2.split("\\.");
			
			if (o1parts.length == 1) {
				if (o2parts.length == 1) return o1.compareTo(o2);
				return -1;
			}
			if (o2parts.length == 1) {
				return 1;
			}
			String firstPart1 = o1parts[0];
			String firstPart2 = o2parts[0];
			if (firstPart1.equals(firstPart2)) {
				String rest1 = o1.substring(o1.indexOf(".") + 1);
				String rest2 = o2.substring(o2.indexOf(".") + 1);
				return this.compare(rest1, rest2);
			}
			else {
				return firstPart1.compareTo(firstPart2);
			}
		}
	}
}
