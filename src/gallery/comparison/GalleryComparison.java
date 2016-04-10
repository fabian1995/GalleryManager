/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gallery.comparison;

import gallery.GalleryNode;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author fabian
 */
public class GalleryComparison {
    
    private final GalleryNode[] gallery = new GalleryNode[2];

    private int statEqualFiles;
    private int statNewFiles;

    @SuppressWarnings("unchecked")
    private final Map<String, File>[] newFiles = new Map[2];

    public GalleryComparison(GalleryNode location0, GalleryNode location1) {

        this.gallery[0] = location0;
        this.gallery[1] = location1;

        this.newFiles[0] = new TreeMap<>();
        this.newFiles[1] = new TreeMap<>();
    }

    public void compare() {

        this.statEqualFiles = 0;
        this.statNewFiles = 0;

        this.newFiles[0].clear();
        this.newFiles[1].clear();

        Map<String, File> buffer = new TreeMap<>();

        for (final File fileEntry : this.gallery[0].listImages()) {
            buffer.put(fileEntry.getName(), fileEntry);
        }

        for (final File fileEntry : this.gallery[1].listImages()) {
            if (buffer.containsKey(fileEntry.getName())) {
                buffer.remove(fileEntry.getName());
                this.statEqualFiles++;
            } else {
                this.newFiles[1].put(fileEntry.getName(), fileEntry);
                this.statNewFiles++;
            }
        }

        this.newFiles[0].putAll(buffer);
        this.statNewFiles += this.newFiles[0].size();
    }

    public String[] getNewFiles(int origin) {

        String[] fileList = new String[this.newFiles[origin].size()];
        int i = 0;

        for (String name : this.newFiles[origin].keySet()) {
            fileList[i] = name;
            i++;
        }

        return fileList;
    }
    
    public GalleryNode getGallery(int origin) {
        return this.gallery[origin];
    }

    public void copy(String fileName, int origin) throws FileNotFoundException, IOException {

        if (!this.newFiles[origin].containsKey(fileName)) {
            throw new FileNotFoundException("File '" + fileName + "' does not exist in location " + origin);
        }

        Path source = this.newFiles[origin].get(fileName).toPath();
        Path dest = new File(this.gallery[origin == 0 ? 1 : 0].getLocation().toPath() + "/" + fileName).toPath();

        Files.copy(source, dest, StandardCopyOption.COPY_ATTRIBUTES);
        this.newFiles[origin].remove(fileName);
        this.statEqualFiles++;
    }

    public void delete(String fileName, int origin) throws FileNotFoundException {

        if (!this.newFiles[origin].containsKey(fileName)) {
            throw new FileNotFoundException("File '" + fileName + "' does not exist in location " + origin);
        }

        this.newFiles[origin].get(fileName).delete();
        this.newFiles[origin].remove(fileName);
        this.statEqualFiles++;
    }

    public void copyAll(int origin) throws IOException {

        for (File file : this.newFiles[origin].values()) {
            Path dest = new File(this.gallery[origin == 0 ? 1 : 0].getLocation().toPath() + "/" + file.getName()).toPath();

            Files.copy(file.toPath(), dest, StandardCopyOption.COPY_ATTRIBUTES);
            this.statEqualFiles++;
        }

        this.newFiles[origin].clear();
    }

    public void deleteAll(int origin) {

        for (File file : this.newFiles[origin].values()) {
            file.delete();
            this.statEqualFiles++;
        }

        this.newFiles[origin].clear();
    }

    public int numberOfEqualFiles() {

        return this.statEqualFiles;
    }

    public int numberOfNewFiles() {

        return this.statNewFiles;
    }

    public void printStats() {

        System.out.println("Stats: new=" + this.statNewFiles + ", equal=" + this.statEqualFiles);

        System.out.println("\nNew to location 1:");
        for (String f : this.newFiles[0].keySet()) {
            System.out.println(" - " + f);
        }

        System.out.println("\nNew to location 2:");
        for (String f : this.newFiles[1].keySet()) {
            System.out.println(" - " + f);
        }
    }
}
