/*******************************************************************************
 * Copyright 2011 IHE International (http://www.ihe.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.ihe.gazelle.common.filter.criterion;

public abstract class AssociationCriterion<E, F> extends AbstractCriterion<E, F> {

	protected String associationPath;

	public AssociationCriterion(Class<F> selectableClass, String keyword, String associationPath) {
		super(selectableClass, keyword);
		this.associationPath = associationPath;
	}

	public String getPath() {
		return associationPath;
	}

}
