package copy.cn.hutool.v_5819.cron.pattern.matcher;


import copy.cn.hutool.v_5819.core.util.StrUtil;

/**
 * 所有值匹配，始终返回{@code true}
 *
 * @author Looly
 */
public class AlwaysTrueMatcher implements PartMatcher {

	public static AlwaysTrueMatcher INSTANCE = new AlwaysTrueMatcher();

	@Override
	public boolean match(Integer t) {
		return true;
	}

	@Override
	public int nextAfter(int value) {
		return value;
	}

	@Override
	public String toString() {
		return StrUtil.format("[Matcher]: always true.");
	}
}
