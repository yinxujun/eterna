������ʹ�õ���java�����Ŀ�Դ���ݿ�H2�����ݿ��ļ���test\WebContent\WEB-INF\db�¡�
��������Ҫ�ı��Ѿ������ˣ��������κθĶ�������eclipse��ֱ�����С���һ������ʱ����Ҫ����һ��Ӧ�÷�������
��ҳ�ĵ�ַΪ��http://����:�˿�/test/test.do


eclipse������Ŀ˵��
1. ��ѹ������ѹ�󣬰�testĿ¼��Ŀ¼���ƿ����޸ģ��ŵ�eclipse�Ĺ������¡�
2. ��eclipse��ѡ��˵�file->import���ڵ���������ѡ��General->Existing Projects into Workspace��Ȼ��ѡ��ո��ƽ�ȥ��testĿ¼����finish��
3. ��Project Explorer�У�ѡ��Deployment Descriptor: test->Servlets->test���Ҽ������ѡ��Run As->Run on Server��
4. �ڵ���������ѡ��һ��Ӧ�÷�����������ʹ��Tomcat v6.0�����ɡ����δ���÷������Ļ�������һ���Ϳ����ˡ�
ע��jdk��Ҫ1.5�����ϰ汾��


�����ʹ�õ���myeclipse�������Ŀ������ߣ�������Ƚ�һ��web��Ŀ��Ȼ��ֱ�src��WebContentĿ¼�µ��ļ����Ƶ���Ӧ��Ŀ¼�¼��ɡ�



micromagic_config.properties�ļ��еļ������õ�˵����

һ��
dataSource.url=jdbc:h2:${h2.baseDir}/test
������ΪH2�����ݿ������ַ�������${h2.baseDir}��Ϊ���ݿ��ļ����ڵ�·�����˱�������test/Test.java���servlet��ʼ��ʱ���ý�ȥ��
���������ݿ��ļ������˱��Ŀ¼�����ֱ���޸�������ã��������һ����h2.baseDir�����ԣ��磺
h2.baseDir=C:\\db
��ʾ���ݿ��ļ���C�̵�dbĿ¼��
h2.baseDir=~
��ʾ���ݿ��ļ��ڵ�ǰ�û�·���£�����ɲ鿴H2��˵���ĵ�

����
self.micromagic.useEternaLog=true
��ʾ����eterna����־�������Ϳ����ڡ�[contextRoot]/eterna/setting.jsp���е�error��־�в鿴�����е���־�����

����
self.micromagic.eterna.digester.checkGrammer=false
��ʾ�ر�ҳ��ű����﷨�ṹ��飬����������߼��ص�Ч�ʡ�
�����ĳ���ű��б�д����Ļ���������һ��"}"�ȣ�����ʼ��ʱ�Ͳ��ᷢ��������󣬴˴��󽫻�ֱ�ӳ�����ҳ���У����ҳ���޷���ʾ��
