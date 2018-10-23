package BugSeverityPrediction ;

import java.util.* ;
import java.io.* ;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.apache.commons.configuration.* ;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class Vectorization
{
	public static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance() ;

	public static HashSet<Integer> loadTargetReportIDs(String fname, String keyword)
	{//xml에서 긁어오는 거라 길다.
		HashSet<Integer> ids = new HashSet<Integer>() ;

		try {//xml라이브러리 
			DocumentBuilder dBuilder = factory.newDocumentBuilder() ;//xml받아오는 공간을 만들어줌  new를 바로하면.. 안좋은점 1. 공간절감 2. 지우기 싫다.. 이럴때 쓰는 한가지 디자인 패턴 
			Document doc = dBuilder.parse(new File(fname)) ;//xml파싱해서 doc에 들어감! 트리 형태로 
			doc.getDocumentElement().normalize() ;

			NodeList reports = doc.getElementsByTagName("report");//트리의 노드...노드들을 긁어노는데 tagname이 report인 아이만.

			for (int i = 0; i < reports.getLength(); i++) {//리포트 하나씩 까봄.
				Element report = (Element) reports.item(i) ;
				int reportID = Integer.parseInt(report.getAttribute("id")) ;

				NodeList updates = report.getElementsByTagName("update") ;
				Node update = updates.item(updates.getLength() - 1) ;//가장 마지막에 있는 업데이트를 받아옴.

				NodeList attr = ((Element) update).getElementsByTagName("what") ;//what에는 컴포넌트 정보가 있음 그 안에 layout이 -1이 아니

				if (attr.item(0).getTextContent().indexOf("Layout") != -1) 
					ids.add(reportID) ;//ids에 넣어라. 
			}
			dBuilder.reset() ;//또 쓸거라...
		} 
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1) ;
		}
		return ids ;//리턴해줌 쓸꺼니까.
	}

	public static HashMap<Integer, Boolean> loadSeverityLabel(String fname, HashSet<Integer> reportIDs)
	{//위에서 긁어온 id에서 관심있는 seve~를 긁어옴. 
		HashMap<Integer, Boolean> labels = new HashMap<Integer, Boolean>() ;

		try {
			DocumentBuilder dBuilder = factory.newDocumentBuilder();
			Document doc = dBuilder.parse(new File(fname));
			doc.getDocumentElement().normalize();

			NodeList reports = doc.getElementsByTagName("report");

			for (int i = 0; i < reports.getLength(); i++) {
				Element report = (Element) reports.item(i);
				int reportID = Integer.parseInt(report.getAttribute("id")) ;

				if (reportIDs.contains(new Integer(reportID)) == false)
					continue ;

				NodeList updates = report.getElementsByTagName("update") ;
				Element lastUpdate = (Element) updates.item(updates.getLength() - 1) ;

				String severity = lastUpdate.getElementsByTagName("what").item(0).getTextContent() ;//what안에 ㄴㄷㅍㄷ갸정보가 있다.
					
				switch (severity) {
					case "critical":
					case "blocker":
					case "major:":
						labels.put(reportID, true) ;
						break ;//위에 세개는 중요한 버그.

					default:
						labels.put(reportID, false) ;
						break ;
				}
			}
			dBuilder.reset() ;
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

		return labels ;
	}

	public static HashMap<Integer, String> loadDescription(String fname, HashSet<Integer> reportIDs)
	{
		HashMap<Integer, String> descriptions = new HashMap<Integer, String>() ;

		try {
			DocumentBuilder dBuilder = factory.newDocumentBuilder();
			Document doc = dBuilder.parse(new File(fname));
			doc.getDocumentElement().normalize();

			NodeList reports = doc.getElementsByTagName("report");

			for (int i = 0; i < reports.getLength(); i++) {
				Element report = (Element) reports.item(i);
				int reportID = Integer.parseInt(report.getAttribute("id")) ;

				if (reportIDs.contains(new Integer(reportID)) == false)
					continue ;

				NodeList updates = report.getElementsByTagName("update") ;
				Element lastUpdate = (Element) updates.item(updates.getLength() - 1) ;
				String description = lastUpdate.getElementsByTagName("what").item(0).getTextContent() ;
				
				descriptions.put(reportID, description) ;
			}
			dBuilder.reset() ;
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

		return descriptions ;//제목을 읽어와 아이디마다 저장함.
	}

	public static TreeMap<String, Integer> buildDictionary(HashMap<Integer, String> descriptions, int threshold) {//descriptions 이거에 맞춰서 만바퀴 돔. 
		TreeMap<String, Integer> dictionary = new TreeMap<String, Integer>() ;
		TreeMap<String, Integer> frequency = new TreeMap<String, Integer>() ;//나온 빈도를 세려고.

		int nWords = 0 ;
		
		for(int i=0; descriptions.)
		//하나 뽑아와서로우 케이스로 바
//stringTokenizer로 description을 썬다.
		//토큰을 보고 나왔으면 +해줌.
		// TO-DO: implement here

		return dictionary ;
	}

	public static double [] getVector(TreeMap<String, Integer> dictionary, String description) {//딕션어리 받고 스트링 받서 토크나이즈 한 뒤 토큰의 아이디에 해당하는 v를 세워줌(1로 해줌)
		description = description.toLowerCase() ;

		double [] v = new double[dictionary.keySet().size()] ;

		// TO-DO: implement here

		return v ;
	}

	public static PropertiesConfiguration loadConfig(String fname) 
	{
		PropertiesConfiguration config = null ;
		try {
			config = new PropertiesConfiguration(fname) ;
		}
		catch (ConfigurationException e) {
			System.err.println(e) ;
			System.exit(1) ;
		}
		return config ;
	}


	public static void main(String[] args)
	{
		HashSet<Integer> 			reportIDs ;
		HashMap<Integer, Boolean>	labels ;
		HashMap<Integer, String> 	descriptions ;//문장 
		TreeMap<String, Integer> 	dictionary ;//단어가 숫자로 맵팽 되어야 함. --> 단어 토큰이 포함되어 있으면 백터로 만들어 --> 제목이 백터로 ㅗ딤.

		PropertiesConfiguration config = loadConfig("config.properties") ;

		reportIDs = loadTargetReportIDs(config.getString("data.dir") + "/component.xml", config.getString("data.module")) ;
		labels = loadSeverityLabel(config.getString("data.dir") + "/severity.xml", reportIDs) ;//reportIDs내가 필요한 데이타에 해당하는 것을 label에 저장 
		descriptions = loadDescription(config.getString("data.dir") + "/short_desc.xml", reportIDs) ;
//위에 메소드로 데이타가 다 모여졌다. 
		dictionary = buildDictionary(descriptions, config.getInt("dictionary.minEvidences")) ;//읽어 온 후 빌드 딕션어리를 함 : 내가 feature로 쓸 데이타를 골라옴(핵심!) 무슨 기준으로 고르느냐.. 최소한 50개의 제목에서 나오는걸 사용하겠다.

		// Print out arff file
		
		try {
			PrintWriter out = new PrintWriter(new FileWriter(config.getString("arff.filename"))) ;

			out.println("@relation bugreport") ;//제목 
			for (int i = 0 ; i < dictionary.keySet().size() ; i++)
				out.println("@attribute c" + i +" numeric") ;//attribute 갯수 : 딕션어리 갯수 
			out.println("@attribute l {nonsevere, severe}") ;//마지막 레이블 
		
			out.println("@data") ;//헤더가 끝남 
			for (Iterator<Integer> i = reportIDs.iterator() ; i.hasNext() ; ) {//reportId v 
				Integer reportID = i.next().intValue() ;

				double [] v = getVector(dictionary, descriptions.get(reportID)) ;//문장을 백터로 만들어줌.
				for (int j = 0 ; j < v.length ; j++)
					out.print(v[j] + ",") ;//만든 문장을 numeric함 

				out.println(labels.get(reportID) ? "severe" : "nonsevere") ;//마지막 레이블 설정해줌.
			}
			out.close() ;
		}//이거 다 끝나고 classfication에 넣으면 자동으로 됨. 즉 이 소스코드에서는 파일만 만들면 됨. 파일의 형태는 arff
		catch (IOException e) {
			System.err.println(e) ;
			System.exit(1) ;
		}
	}
}
