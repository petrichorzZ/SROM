package com.sorm.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sorm.bean.ColumnInfo;
import com.sorm.bean.JavaFieldGetSet;
import com.sorm.bean.TableInfo;
import com.sorm.core.DBManager;
import com.sorm.core.MysqlTypeConvertor;
import com.sorm.core.TableContext;
import com.sorm.core.TypeConvertor;

/**
 * ��װ���ַ������ò���
 * @author Administrator
 *
 */
public class JavaFileUtils {
	private static  JavaFieldGetSet createFieldGetSetSrc(ColumnInfo column,TypeConvertor convertor) {
		JavaFieldGetSet jfgs = new JavaFieldGetSet();
		String javaFieldType = convertor.databaseTypeToJavaType(column.getDataType());
		
		jfgs.setFieldInfo("\tprivate "+javaFieldType+" "+column.getName()+";\n");
		
		StringBuilder getSrc = new StringBuilder();
		getSrc.append("\tpublic "+javaFieldType+" "+"get"+StringUtils.firstCharToUpperCase(column.getName())+"(){\n");
		getSrc.append("\t\treturn "+column.getName()+";\n");
		getSrc.append("\t}\n");
		jfgs.setGetInfo(getSrc.toString());
		
		StringBuilder setSrc = new StringBuilder();
		setSrc.append("\tpublic void set"+StringUtils.firstCharToUpperCase(column.getName())+"("+javaFieldType+" "+column.getName()+"){\n");
		setSrc.append("\t\tthis."+column.getName()+" = "+column.getName()+";\n");
		setSrc.append("\t}\n");
		jfgs.setSetInfo(setSrc.toString());
	
		return jfgs;
		
	}
	
	private static String createJavaSrc(TableInfo tableInfo,TypeConvertor convertor) {
		StringBuilder src = new StringBuilder();
		Map<String,ColumnInfo> columns = tableInfo.getColumns();
		List<JavaFieldGetSet> javaFields = new ArrayList<>();
		for(ColumnInfo columnInfo:columns.values()) {
			javaFields.add(createFieldGetSetSrc(columnInfo,convertor));
		}
		
		//����package���
		src.append("package "+DBManager.getConfiguration().getPoPackage()+";\n");
		//����import���
		src.append("import java.sql.*;\n");
		src.append("import java.util.*;\n");
		//�������������
		src.append("public class "+StringUtils.firstCharToUpperCase(tableInfo.getTname())+" {\n\n");
		//���������б�
		for(JavaFieldGetSet jfsg:javaFields) {
			src.append(jfsg.getFieldInfo());
		}
		src.append("\n\n");
		//���ɿչ�����
		src.append("\tpublic "+StringUtils.firstCharToUpperCase(tableInfo.getTname())+" () {}\n");
		src.append("\n\n");
		//����get����
		for(JavaFieldGetSet jfsg:javaFields) {
			src.append(jfsg.getGetInfo());
		}
		src.append("\n\n");
		//����set����
		for(JavaFieldGetSet jfsg:javaFields) {
			src.append(jfsg.getSetInfo());
		}
		src.append("\n\n");
		//���ɽ�����
		src.append("}\n");
		return src.toString();
	}
	
	//����java�ļ�
	public static void createJavaPOFile(TableInfo tableInfo, TypeConvertor convertor) {
		String src = createJavaSrc(tableInfo, convertor);
		String srcPath = DBManager.getConfiguration().getSrcPath()+"\\";
		String packagePath = DBManager.getConfiguration().getPoPackage().replaceAll("\\.", "\\\\");
		File f = new File(srcPath+packagePath);
//		System.out.println(f.getAbsolutePath());
		if(!f.exists()) {//ָ��Ŀ¼�����ڣ�����
			f.mkdirs();
		}
		BufferedWriter bw = null;
		try {
			bw =new BufferedWriter(new FileWriter(f.getAbsolutePath()+"/"+StringUtils.firstCharToUpperCase(tableInfo.getTname())+".java"));
			bw.write(src);
			bw.flush();
			System.out.println("������"+tableInfo.getTname()+"��Ӧ��java��"+StringUtils.firstCharToUpperCase(tableInfo.getTname())+".java");
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(bw!=null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
