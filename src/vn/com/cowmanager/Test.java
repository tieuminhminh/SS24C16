package vn.com.cowmanager;

import vn.com.cowmanager.model.feeding.Food;
import vn.com.cowmanager.model.feeding.Storage;

public class Test {

	public static void main(String[] args) {
		Food f1 = new Food("abc", "food", 12.0, "12/05/2018", "11/12/2020", "fsdajfns");
		Storage s1 = new Storage(f1, "", 0.0, 300, null, 0, "");
		System.out.println(s1.getProductName());
	}
}
