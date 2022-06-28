(ns pdfshield.core
  (:require [clojure.java.io :as io])
  (:import [java.io ByteArrayOutputStream File FileInputStream FileOutputStream]
           [java.util ArrayList]
           [org.apache.fontbox.ttf TrueTypeCollection]
           [org.apache.pdfbox.io MemoryUsageSetting]
           [org.apache.pdfbox.multipdf PDFMergerUtility]
           [org.apache.pdfbox.pdmodel.graphics.image PDImageXObject]
           [org.apache.pdfbox.pdmodel PDDocument PDPageContentStream PDPageContentStream$AppendMode]
           [org.apache.pdfbox.pdmodel.encryption AccessPermission StandardProtectionPolicy]
           [org.apache.pdfbox.pdmodel.font PDType0Font])
  (:gen-class))

(defn set-text [contents page-num font-size x y text]
  (let [document (PDDocument/load contents)
        page (.getPage document page-num)
        ttc (TrueTypeCollection. (File. "resources/msgothic.ttc"))
        ttf (.getFontByName ttc "MS-Gothic")
        font (PDType0Font/load document ttf true)
        bos (ByteArrayOutputStream.)]
    (doto (PDPageContentStream. document page PDPageContentStream$AppendMode/APPEND true true)
      (.beginText)
      (.setFont font font-size)
      (.newLineAtOffset x y)
      (.showText text)
      (.endText)
      (.close))
    (.save document bos)
    (.close document)
    (.toByteArray bos)))

(defn set-image [contents img-contents page-num x y width height]
  (let [document (PDDocument/load contents)
        page (.getPage document page-num)
        imgbytes (.readAllBytes img-contents)
        img (PDImageXObject/createFromByteArray document imgbytes "img")
        bos (ByteArrayOutputStream.)]
    (doto (PDPageContentStream. document page PDPageContentStream$AppendMode/APPEND true true)
      (.drawImage img x y width height)
      (.close))
    (.save document bos)
    (.close document)
    (.toByteArray bos)))

(defn extract-pages [contents start-page end-page]
  (let [document (PDDocument/load contents)
        new-document (PDDocument.)
        bos (ByteArrayOutputStream.)]
    (reduce #(.addPage new-document (.getPage document %2))
            nil
            (range start-page (inc end-page)))
    (.save new-document bos)
    (.close new-document)
    (.toByteArray bos)))

(defn merge-documents [& contents]
  (let [bos (ByteArrayOutputStream.)]
    (doto (PDFMergerUtility.)
      (.addSources (ArrayList. contents))
      (.setDestinationStream bos)
      (.mergeDocuments (MemoryUsageSetting/setupMainMemoryOnly)))
    (.toByteArray bos)))

(defn remove-page [contents & page-nums]
  (let [document (PDDocument/load contents)
        bos (ByteArrayOutputStream.)]
    (reduce #(do
               (.removePage document (- %2 %1))
               (inc %1))
            0
            (sort page-nums))
    (.save document bos)
    (.close document)
    (.toByteArray bos)))

(defn encrypt-page [contents owner-pass user-pass]
  (let [document (PDDocument/load contents)
        spp (StandardProtectionPolicy. owner-pass user-pass (AccessPermission.))
        bos (ByteArrayOutputStream.)]
    (.setEncryptionKeyLength spp 256)
    (.protect document spp)
    (.save document bos)
    (.close document)
    (.toByteArray bos)))

(defn -main
  [& args]
  (case (first args)
    "set-text" (let [input-filename (second args)
                     page-num (Integer/parseInt (nth args 2))
                     font-size (Float/parseFloat (nth args 3))
                     x (Float/parseFloat (nth args 4))
                     y (Float/parseFloat (nth args 5))
                     text (nth args 6)
                     output-filename (nth args 7)
                     fis (FileInputStream. input-filename)
                     byte-arr (set-text fis page-num font-size x y text)
                     fout (io/as-file output-filename)]
                 (doto (FileOutputStream. fout)
                   (.write byte-arr)
                   (.close)))
    "set-image" (let [input-filename (second args)
                      img-filename (nth args 2)
                      page-num (Integer/parseInt (nth args 3))
                      x (Float/parseFloat (nth args 4))
                      y (Float/parseFloat (nth args 5))
                      width (Float/parseFloat (nth args 6))
                      height (Float/parseFloat (nth args 7))
                      output-filename (nth args 8)
                      fis (FileInputStream. input-filename)
                      imgfis (FileInputStream. img-filename)
                      byte-arr (set-image fis imgfis page-num x y width height)
                      fout (io/as-file output-filename)]
                  (doto (FileOutputStream. fout)
                    (.write byte-arr)
                    (.close)))
    "extract-pages" (let [input-filename (second args)
                         start-page (Integer/parseInt (nth args 2))
                         end-page (Integer/parseInt (nth args 3))
                         output-filename (nth args 4)
                         fis (FileInputStream. input-filename)
                         byte-arr (extract-pages fis start-page end-page)
                         fout (io/as-file output-filename)]
                     (doto (FileOutputStream. fout)
                       (.write byte-arr)
                       (.close)))
    "merge-documents" (let [output-filename (last args)
                            byte-arr (apply merge-documents (map #(FileInputStream. %) (rest (drop-last args))))
                            fout (io/as-file output-filename)]
                        (doto (FileOutputStream. fout)
                          (.write byte-arr)
                          (.close)))
    (println "default")))
