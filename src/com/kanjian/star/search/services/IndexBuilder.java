package com.kanjian.star.search.services;

import java.util.HashMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;

import com.kanjian.star.search.dao.StarDAO;


@Component
@Service
public class IndexBuilder {

	private static final Logger logger = Logger.getLogger(IndexBuilder.class);

    @Autowired
    private StarDAO dao;

    @Value("${hop_each_build}")
    private String hopEachBuild;

	@Value("${index_original_path}")
	private String indexOriginalPath;

	@Value("${max_memory}")
	private String maxMemory;

	public static final FieldType INT_FIELD_TYPE_STORED_SORTED = new FieldType();
	static {
		INT_FIELD_TYPE_STORED_SORTED.setTokenized(true);
		INT_FIELD_TYPE_STORED_SORTED.setOmitNorms(true);
		INT_FIELD_TYPE_STORED_SORTED.setIndexOptions(IndexOptions.DOCS);
		INT_FIELD_TYPE_STORED_SORTED.setNumericType(FieldType.NumericType.INT);
		INT_FIELD_TYPE_STORED_SORTED.setStored(true);
		INT_FIELD_TYPE_STORED_SORTED.setDocValuesType(DocValuesType.NUMERIC);
		INT_FIELD_TYPE_STORED_SORTED.freeze();
	}

    public int buildIndex() throws IOException {
        buildTrackIndex();
        buildAlbumIndex();
        return 0;
    }

    public int buildTrackIndex() throws IOException {

        Analyzer analyzer = new StandardAnalyzer();
        String trackIndexPath = indexOriginalPath + "/track";
        Directory dir = FSDirectory.open(Paths.get(trackIndexPath));
        IndexWriterConfig indexWriter = new IndexWriterConfig(analyzer);
        indexWriter.setOpenMode(OpenMode.CREATE);
        indexWriter.setRAMBufferSizeMB(Integer.parseInt(maxMemory));
        IndexWriter writer = new IndexWriter(dir, indexWriter);

        int maxId = dao.getTrackMaxId();

        int count_per_query = Integer.parseInt(hopEachBuild);
        for (int i = 0;(i+1) * count_per_query <= maxId+1;i++) {
            for (HashMap<String, Object> track: dao.getTrackList(i*count_per_query, (i+1)*count_per_query)) {
				Document doc = new Document();
                doc.add(new IntField("id", (Integer)track.get("id"), INT_FIELD_TYPE_STORED_SORTED));
                String title = (String)track.get("name");
                if (track.get("version") != null) {
                    title += " " + (String)track.get("version");
                }
                if (track.get("english_name") != null) {
                    title += " " + (String)track.get("english_name");
                }
                doc.add(new TextField("title", title + " " + "kanjiansdxqfx", Field.Store.NO));
                if (track.get("duration") != null) {
                    doc.add(new IntField("duration", (Integer)track.get("duration"), Field.Store.NO));
                }
                writer.addDocument(doc);
            }
        }

        writer.close();
        return 0;
    }
    public int buildAlbumIndex() throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        String albumIndexPath = indexOriginalPath + "/album";
        Directory dir = FSDirectory.open(Paths.get(albumIndexPath));
        IndexWriterConfig indexWriter = new IndexWriterConfig(analyzer);
        indexWriter.setOpenMode(OpenMode.CREATE);
        indexWriter.setRAMBufferSizeMB(Integer.parseInt(maxMemory));
        IndexWriter writer = new IndexWriter(dir, indexWriter);

        int maxId = dao.getAlbumMaxId();
        int count_per_query = Integer.parseInt(hopEachBuild);
        for (int i = 0;(i+1) * count_per_query <= maxId+1;i++) {
            for (HashMap<String, Object> album: dao.getAlbumList(i*count_per_query, (i+1)*count_per_query)) {
				Document doc = new Document();
                doc.add(new IntField("id", (Integer)album.get("id"), INT_FIELD_TYPE_STORED_SORTED));
                String title = (String)album.get("name");
                if (album.get("version") != null) {
                    title += " " + (String)album.get("version");
                }
                doc.add(new TextField("title", title + " " + "kanjiansdxqfx", Field.Store.NO));
                writer.addDocument(doc);
            }
        }

        writer.close();
        return 0;
    }
}
