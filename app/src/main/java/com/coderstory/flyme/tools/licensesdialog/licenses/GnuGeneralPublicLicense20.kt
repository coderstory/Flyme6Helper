/*
 * Copyright 2014 Shaleen Jain <shaleen.jain95@gmail.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.coderstory.flyme.tools.licensesdialog.licenses

import android.content.Context
import com.coderstory.flyme.R

class GnuGeneralPublicLicense20 : License() {
    override fun getName(): String {
        return "GNU General Public License 2.0"
    }

    override fun readSummaryTextFromResources(context: Context?): String? {
        return getContent(context, R.raw.gpl_20_summary)
    }

    override fun readFullTextFromResources(context: Context?): String? {
        return getContent(context, R.raw.gpl_20_full)
    }

    override fun getVersion(): String {
        return "2.0"
    }

    override fun getUrl(): String {
        return "http://www.gnu.org/licenses/"
    }
}