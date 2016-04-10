/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gallery;

/**
 *
 * @author fabian
 */
public class TestApp {
    public static void main(String[] args) {
        String path1 = "/home/fabian/loc1/loc2/hello/world";
        String path2 = "/home/fabian";
        
        path1 = path1.replaceFirst(path2, "");
        
        System.out.println("->" + path1 + " () " + path1.lastIndexOf(path2));
    }
}
        
        