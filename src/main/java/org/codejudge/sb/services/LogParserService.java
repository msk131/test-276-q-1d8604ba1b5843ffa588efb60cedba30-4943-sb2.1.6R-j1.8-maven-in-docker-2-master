package org.codejudge.sb.services;

import java.util.Set;

import org.codejudge.sb.model.RequestLogModel;
import org.codejudge.sb.model.ResponseLogModel;

public interface LogParserService {

	Set<ResponseLogModel> parseLogFiles(RequestLogModel logDetail);
}
