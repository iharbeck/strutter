/*
 * Copyright 2006 Ingo Harbeck.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.action.webservice;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;

import strutter.config.tags.ConfigWSInterface;
import strutter.helper.ActionHelper;
import strutter.helper.WSActionHelper;

public class WebserviceJpegAction implements ConfigWSInterface
{
	public void doGet() throws Exception
	{
		WSActionHelper.setContentType(WSActionHelper.TYPE_JPEG);

		File f = new File(getClass().getResource("ingo.jpg").getFile());

		BufferedImage img = ImageIO.read(new FileInputStream(f));
		
		img.getGraphics().draw3DRect(3, 3, 110, 110, true);

		// Send back image
		ServletOutputStream sos = ActionHelper.getResponse().getOutputStream();
		
		ImageIO.write(img, "jpeg", sos);
	}
}