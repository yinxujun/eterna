������ʹ�õ���java�����Ŀ�Դ���ݿ�H2�����ݿ��ļ���test\WebContent\WEB-INF\db�¡�
��������Ҫ�ı��Ѿ������ˣ��������κθĶ�������eclipse��ֱ�����С���һ������ʱ����Ҫѡ��һ��Ӧ�÷�������


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
