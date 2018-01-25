package com.sb.base.core.data.jpa;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class Condition<T> implements Specification<T> {

	public enum Operator {
		EQ, IN, LIKE, GT, LT, GTE, LTE, NOT;

		public static Operator fromString(String value) {
			try {
				return Operator.valueOf(value.toUpperCase(Locale.US));
			} catch (Exception e) {
				throw new IllegalArgumentException(String.format(
						"Invalid value '%s' for Operator given! Has to be in 'eq, like, gt, lt, gte, lte, not' (case insensitive).", value), e);
			}
		}
	}

	private Condition() {
	}

	private List<Filter> filters = Lists.newArrayList();

	public static <T> Condition<T> build() {
		return new Condition<T>();
	}

	public Condition<T> clear() {
		filters.clear();
		return this;
	}

	public Condition<T> put(String field, Object value) {
		filters.add(new Filter(field, value));
		return this;
	}

	public Condition<T> put(String field, Operator op, Object value) {
		filters.add(new Filter(field, op, value));
		return this;
	}

	public Condition<T> put(Map<String, Object> params) {
		for (Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			String[] names = StringUtils.split(key, ":");
			Operator operator = names.length == 1 ? Operator.EQ : Operator.fromString(names[1]);
			filters.add(new Filter(names[0], operator, entry.getValue()));
		}
		return this;
	}

	public Condition<T> merge(Condition<T> temp) {
		this.filters.addAll(temp.filters);
		return this;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		if (filters.isEmpty()) {
			return cb.conjunction();
		}

		List<Predicate> predicates = Lists.newArrayList();
		for (Filter filter : filters) {
			String[] names = StringUtils.split(filter.field, ".");
			Path<String> expression = root.get(names[0]);
			for (int i = 1; i < names.length; i++) {
				expression = expression.get(names[i]);
			}
			switch (filter.operator) {
			case EQ:
				if (filter.value == null) {
					predicates.add(cb.isNull(expression));
				} else {
					predicates.add(cb.equal(expression, filter.value));
				}
				break;
			case IN:
				Object value = filter.value;
				Collection values = null;

				if (value instanceof Collection) {
					values = (Collection) value;
				} else if (value instanceof String) {
					values = Splitter.on(",").splitToList((String) value);
				}

				if (values != null) {
					if (values.size() == 1) {
						// 当in的值只有一个时，优化成EQ查询
						predicates.add(cb.equal(expression, Iterators.get(values.iterator(), 0)));
					} else {
						In in = cb.in(expression);
						Iterator iterator = values.iterator();
						while (iterator.hasNext()) {
							in.value(iterator.next());
						}
						predicates.add(in);
					}
				}
				break;
			case LIKE:
				predicates.add(cb.like(expression, "%" + filter.value + "%"));
				break;
			case GT:
				predicates.add(cb.greaterThan(expression, (Comparable) filter.value));
				break;
			case LT:
				predicates.add(cb.lessThan(expression, (Comparable) filter.value));
				break;
			case GTE:
				predicates.add(cb.greaterThanOrEqualTo(expression, (Comparable) filter.value));
				break;
			case LTE:
				predicates.add(cb.lessThanOrEqualTo(expression, (Comparable) filter.value));
				break;
			case NOT:
				if (filter.value == null) {
					predicates.add(cb.isNotNull(expression));
				} else {
					predicates.add(cb.notEqual(expression, filter.value));
				}
			default:
			}
		}

		return cb.and(predicates.toArray(new Predicate[predicates.size()]));
	}

	class Filter {
		protected String field;
		protected Object value;
		protected Operator operator;

		public Filter(String field, Object value) {
			this.field = field;
			this.value = value;
			this.operator = Operator.EQ;
		}

		public Filter(String field, Operator operator, Object value) {
			this.field = field;
			this.value = value;
			this.operator = operator;
		}
	}
}
