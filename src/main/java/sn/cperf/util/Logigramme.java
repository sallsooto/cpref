package sn.cperf.util;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Logigramme {
	private Long id;
	private String label;
	private List<String> text;
	private String type;
	private String yes;
	private String no;
	private Map<String,Object> tip;
	private List<String> employes;
}
