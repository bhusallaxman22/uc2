package uc2.repository;

import java.util.List;
import uc2.domain.BusinessRule;

public interface RuleRepository {
    List<BusinessRule> findAll();

    void replaceAll(List<BusinessRule> rules);
}
