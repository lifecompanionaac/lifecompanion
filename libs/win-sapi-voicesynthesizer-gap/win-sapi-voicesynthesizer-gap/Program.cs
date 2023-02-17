/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2021 CMRRF KERPAPE (Lorient, France)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

using System;
using System.Collections.Generic;
using System.Text;
using System.Net;
using System.IO;
using System.Threading;
using System.Speech.Synthesis;

namespace LifeCompanion_VoiceSynthesizer
{
    class Launcher
    {
        static void Main(string[] args)
        {
            new LifeCompanionVoiceSynthesizer(int.Parse(args[0]));
        }
    }

    class LifeCompanionVoiceSynthesizer
    {
        private Thread serverThread;
        private HttpListener listener;
        private int port;
        private SpeechSynthesizer speechSynthesizer;
        public bool running = true;

        public LifeCompanionVoiceSynthesizer(int port)
        {
            this.Initialize(port);
            this.speechSynthesizer = new SpeechSynthesizer();
            Console.WriteLine("SpeechSynthesizer initialized");
        }

        private void Initialize(int port)
        {
            this.port = port;
            serverThread = new Thread(this.Listen);
            serverThread.Start();
        }

        public void Stop()
        {
            serverThread.Abort();
            listener.Stop();
        }

        private void Listen()
        {
            listener = new HttpListener();
            listener.Prefixes.Add("http://localhost:" + port.ToString() + "/");
            listener.Start();
            Console.WriteLine("Listener initialized");
            while (running)
            {
                try
                {
                    HttpListenerContext context = listener.GetContext();
                    Thread workerThread = new Thread(new SpeechWorker(this, context, speechSynthesizer).work);
                    workerThread.Start();
                }
                catch (Exception)
                {
                    // Ignore error that could happen
                }
            }
        }


    }

    class SpeechWorker
    {
        private HttpListenerContext context;
        private SpeechSynthesizer speechSynthesizer;
        private LifeCompanionVoiceSynthesizer lifeCompanionVoiceSynthesizer;
        private String wav;

        public SpeechWorker(LifeCompanionVoiceSynthesizer lifeCompanionVoiceSynthesizer, HttpListenerContext context, SpeechSynthesizer speechSynthesizer)
        {
            this.context = context;
            this.speechSynthesizer = speechSynthesizer;
            this.lifeCompanionVoiceSynthesizer = lifeCompanionVoiceSynthesizer;
        }

        private void configureBeforeSpeak()
        {
            // Configure speech synthesizer
            if (!String.IsNullOrEmpty(context.Request.QueryString["volume"]))
            {
                this.speechSynthesizer.Volume = Convert.ToInt32(context.Request.QueryString["volume"]);
            }
            if (!String.IsNullOrEmpty(context.Request.QueryString["rate"]))
            {
                this.speechSynthesizer.Rate = Convert.ToInt32(context.Request.QueryString["rate"]);
            }
            if (!String.IsNullOrEmpty(context.Request.QueryString["voice"]))
            {
                this.speechSynthesizer.SelectVoice(context.Request.QueryString["voice"]);
            }
            if (!String.IsNullOrEmpty(context.Request.QueryString["wav"]))
            {
                this.wav = context.Request.QueryString["wav"];
            }
            else
            {
                this.wav = null;
            }
        }

        public void work()
        {
            try
            {
                using (System.IO.StreamWriter sw = new System.IO.StreamWriter(context.Response.OutputStream))
                {
                    string urlPath = context.Request.Url.AbsolutePath.Substring(1);
                    switch (urlPath)
                    {
                        case "stop":
                            this.speechSynthesizer.SpeakAsyncCancelAll();
                            break;
                        case "get-voices":
                            ICollection<InstalledVoice> voices = speechSynthesizer.GetInstalledVoices();
                            string voiceJson = "[\n";
                            Boolean first = true;
                            foreach (InstalledVoice voice in voices)
                            {
                                voiceJson += !first ? ",\n" : "";
                                voiceJson += "{ \"name\":\"" + voice.VoiceInfo.Name + "\", \"language\":\"" + voice.VoiceInfo.Culture.ToString() + "\", \"gender\":\"" + voice.VoiceInfo.Gender.ToString() + "\" }";
                                first = false;
                            }
                            sw.WriteLine(voiceJson + "\n]");
                            break;
                        case "speak":
                            configureBeforeSpeak();
                            // Speak content
                            using (StreamReader reader = new StreamReader(context.Request.InputStream, Encoding.UTF8))
                            {
                                string contentToRead = reader.ReadToEnd();
                                if (contentToRead != null && contentToRead.Trim().Length != 0)
                                {
                                    try
                                    {
                                        string wavPath = System.IO.Path.GetTempPath() + Guid.NewGuid().ToString() + ".wav";
                                        if (!String.IsNullOrEmpty(this.wav))
                                        {
                                            this.speechSynthesizer.SetOutputToWaveFile(wavPath);
                                            sw.Write(wavPath);
                                        }
                                        else
                                        {
                                            this.speechSynthesizer.SetOutputToDefaultAudioDevice();
                                        }
                                       this.speechSynthesizer.Speak(contentToRead);
                                    }
                                    catch (OperationCanceledException)
                                    {
                                        // Ok to ignore (cancel is possible)
                                    }
                                }
                            }
                            break;
                        case "speak-ssml":
                            configureBeforeSpeak();
                            // Speak content
                            using (StreamReader reader = new StreamReader(context.Request.InputStream, Encoding.UTF8))
                            {
                                string contentToRead = reader.ReadToEnd();
                                if (contentToRead != null && contentToRead.Trim().Length != 0)
                                {
                                    try
                                    {
                                        this.speechSynthesizer.SpeakSsml(contentToRead);
                                    }
                                    catch (OperationCanceledException e)
                                    {
                                        // Ok to ignore (cancel is possible)
                                    }
                                }
                            }
                            break;
                        case "dispose":
                            this.speechSynthesizer.SpeakAsyncCancelAll();
                            this.speechSynthesizer.Dispose();
                            lifeCompanionVoiceSynthesizer.running = false;
                            break;
                        default:
                            sw.Write("{\"error\" : \"Bad URL\"}");
                            break;
                    }
                }
            }
            catch (Exception)
            {
                // Ignore errors
            }
        }
    }
}