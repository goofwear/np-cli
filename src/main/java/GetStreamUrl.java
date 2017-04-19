import org.schabi.newpipe.extractor.Downloader;
import org.schabi.newpipe.extractor.MediaFormat;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;
import org.schabi.newpipe.extractor.stream_info.StreamExtractor;
import org.schabi.newpipe.extractor.stream_info.StreamInfo;
import org.schabi.newpipe.extractor.stream_info.VideoStream;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Christian Schabesberger on 01.03.17.
 *
 * Copyright (C) Christian Schabesberger 2017 <chris.schabesberger@mailbox.org>
 * GetStreamUrl.java is part of NewPipe.
 *
 * NewPipe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NewPipe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NewPipe.  If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * This class will demonstreate a simple NewPipe operation.
 * Provide a youtube link as first parameter, and you will get the link
 * to a 320p stream url. So you can download the stream via wget or curl.
 */

public class GetStreamUrl {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0";

    public static void main(String argv[]) {
        if(argv.length == 0) {
            System.err.println("please provide a youtube url as first parameter.");
            return;
        }

        String url = argv[0];

        //first we need to set the Downloader, so NewPipe knows how to get the files
        NewPipe.init(initDownloader());
        try {
            StreamingService youtube = NewPipe.getService("Youtube");
            StreamExtractor extractor = youtube.getExtractorInstance(url);

            // actual extraction
            StreamInfo streamInfo = StreamInfo.getVideoInfo(extractor);

            // if non critical exceptions happened during extraction they will be printed now
            for(Throwable error : streamInfo.errors) {
                System.err.println("----------------");
                error.printStackTrace();
            }

            // now print the stream url and we are done
            for(VideoStream stream : streamInfo.video_streams) {
                if(stream.resolution.contains("320p") ||
                        stream.resolution.contains("720p") ||
                        stream.resolution.contains("360p")) {
                    System.out.print(stream.url);
                    return;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Downloader initDownloader() {
        return new Downloader() {
            @Override
            public String download(String siteUrl, String language) throws IOException, ReCaptchaException {
                Map<String, String> requestProperties = new HashMap<>();
                requestProperties.put("Accept-Language", language);
                return download(siteUrl, requestProperties);
            }

            @Override
            public String download(String siteUrl, Map<String, String> customProperties) throws IOException, ReCaptchaException {
                URL url = new URL(siteUrl);
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                BufferedReader in = null;
                StringBuilder response = new StringBuilder();

                try {
                    con.setRequestMethod("GET");
                    con.setRequestProperty("User-Agent", USER_AGENT);

                    in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;

                    while((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                } catch(UnknownHostException uhe) {//thrown when there's no internet connection
                    throw new IOException("unknown host or no network", uhe);
                    //Toast.makeText(getActivity(), uhe.getMessage(), Toast.LENGTH_LONG).show();
                } catch(Exception e) {
            /*
             * HTTP 429 == Too Many Request
             * Receive from Youtube.com = ReCaptcha challenge request
             * See : https://github.com/rg3/youtube-dl/issues/5138
             */
                    if (con.getResponseCode() == 429) {
                        throw new ReCaptchaException("reCaptcha Challenge requested");
                    }
                    throw new IOException(e);
                } finally {
                    if(in != null) {
                        in.close();
                    }
                }

                return response.toString();
            }

            @Override
            public String download(String siteUrl) throws IOException, ReCaptchaException {
                Map<String, String> requestProperties = new HashMap<>();
                return download(siteUrl, requestProperties);
            }
        };
    }
}
