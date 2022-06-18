(ns pdfshield.core
  (:import [java.io ByteArrayOutputStream File FileInputStream FileOutputStream]
           [org.apache.fontbox.ttf TrueTypeCollection]
           [org.apache.pdfbox.pdmodel.graphics.image PDImageXObject]
           [org.apache.pdfbox.pdmodel PDDocument PDPageContentStream PDPageContentStream$AppendMode]
           [org.apache.pdfbox.pdmodel.font PDType0Font])
  (:gen-class))

(defn set-text [contents page-num size x y text]
  (let [document (PDDocument/load contents)
        page (.getPage document page-num)
        ttc (TrueTypeCollection. (File. "resources/msgothic.ttc"))
        ttf (.getFontByName ttc "MS-Gothic")
        font (PDType0Font/load document ttf true)
        bos (ByteArrayOutputStream.)]
    (doto (PDPageContentStream. document page PDPageContentStream$AppendMode/APPEND true true)
      (.beginText)
      (.setFont font size)
      (.newLineAtOffset x y)
      (.showText text)
      (.endText)
      (.close))
    (.save document bos)
    (.close document)
    (.toByteArray bos)))

(defn test-set-text []
  (let [fis (FileInputStream. "resources/test.pdf")
        byte-arr (set-text fis 1 20 100 200 "テストテスト")]
    (doto (FileOutputStream. "resources/test2.pdf")
      (.write byte-arr)
      (.close))))

(defn set-image [contents img-ins page-num x y width height]
  (let [document (PDDocument/load contents)
        page (.getPage document page-num)
        img-byte (.readAllBytes img-ins)
        img (PDImageXObject/createFromByteArray document img-byte "img")
        bos (ByteArrayOutputStream.)]
    (doto (PDPageContentStream. document page PDPageContentStream$AppendMode/APPEND true true)
      (.drawImage img x y width height)
      (.close))
    (.save document bos)
    (.close document)
    (.toByteArray bos)))

(defn test-set-image []
  (let [fis (FileInputStream. "resources/test.pdf")
        img-ins (FileInputStream. "resources/test.png")
        byte-arr (set-image fis img-ins 1 (float 20) (float 30) (float 100) (float 200))]
    (doto (FileOutputStream. "resources/test3.pdf")
      (.write byte-arr)
      (.close))))

(defn split-page [contents start-page end-page]
  (let [document (PDDocument/load contents)
        new-document (PDDocument.)
        bos (ByteArrayOutputStream.)]
    (reduce #(.addPage new-document (.getPage document %2))
            nil
            (range start-page (inc end-page)))
    (.save new-document bos)
    (.close new-document)
    (.toByteArray bos)))

(defn test-split-page []
  (let [fis (FileInputStream. "resources/test.pdf")
        byte-arr (split-page fis 2 4)]
    (doto (FileOutputStream. "resources/test4.pdf")
      (.write byte-arr)
      (.close))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
